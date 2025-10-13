-- CoolGuiServer.lua
-- Server-side handler for CoolGUI world effects (FE-safe).
-- Place in ServerScriptService.

local ReplicatedStorage = game:GetService("ReplicatedStorage")
local Lighting = game:GetService("Lighting")
local SoundService = game:GetService("SoundService")
local RunService = game:GetService("RunService")
local Workspace = game:GetService("Workspace")

local REMOTE_NAME = "CoolGuiEvent"
local remote = ReplicatedStorage:FindFirstChild(REMOTE_NAME)
if not remote then
	remote = Instance.new("RemoteEvent")
	remote.Name = REMOTE_NAME
	remote.Parent = ReplicatedStorage
end

-- Store original properties for Reset
local original = {
	parts = {}, -- key -> {Instance = part, Color, Material, Transparency}
	lighting = {
		Ambient = Lighting.Ambient,
		OutdoorAmbient = Lighting.OutdoorAmbient,
		TimeOfDay = Lighting.ClockTime or Lighting.TimeOfDay,
		Brightness = Lighting.Brightness,
		ColorCorrection = nil,
		Bloom = nil,
		Sky = Lighting:FindFirstChildOfClass("Sky")
	},
	sky = nil,
}

-- helper: unique key for instance
local function keyFor(inst)
	return tostring(inst:GetDebugId())
end

-- capture current workspace parts (only BaseParts) so we can reset later
local function captureOriginalParts()
	for _, p in ipairs(Workspace:GetDescendants()) do
		if p:IsA("BasePart") then
			local k = keyFor(p)
			if not original.parts[k] then
				original.parts[k] = {
					Instance = p,
					Color = p.Color,
					Material = p.Material,
					Transparency = p.Transparency,
				}
			end
		end
	end
end

-- Make everything red (applies to existing parts only)
local function makeEverythingRed()
	captureOriginalParts()
	for _, info in pairs(original.parts) do
		local p = info.Instance
		if p and p.Parent then
			p.Color = Color3.fromRGB(255, 0, 0)
			p.Material = Enum.Material.SmoothPlastic
			p.Transparency = 0
		end
	end
	-- Lighting tint
	local cc = Lighting:FindFirstChild("CoolGuiColorCorrection")
	if not cc then
		cc = Instance.new("ColorCorrectionEffect")
		cc.Name = "CoolGuiColorCorrection"
		cc.Parent = Lighting
	end
	cc.TintColor = Color3.fromRGB(255, 80, 80)
	cc.Contrast = 0.15
	cc.Saturation = -0.15

	local bloom = Lighting:FindFirstChild("CoolGuiBloom")
	if not bloom then
		bloom = Instance.new("BloomEffect")
		bloom.Name = "CoolGuiBloom"
		bloom.Intensity = 1
		bloom.Parent = Lighting
	end
end

-- Change skybox: params should be table {SkyboxId} or expected fields
local function setSkybox(skyasset)
	-- skyasset can be an asset id string like "rbxassetid://123456" or a table of ids (not required)
	local existing = Lighting:FindFirstChildOfClass("Sky")
	if existing and existing:IsA("Sky") then
		original.lighting.Sky = existing:Clone()
	end

	local newSky = Instance.new("Sky")
	newSky.Name = "CoolGuiSky"
	if type(skyasset) == "string" then
		newSky.SkyboxBk = skyasset
		newSky.SkyboxDn = skyasset
		newSky.SkyboxFt = skyasset
		newSky.SkyboxLf = skyasset
		newSky.SkyboxRt = skyasset
		newSky.SkyboxUp = skyasset
	elseif type(skyasset) == "table" then
		-- accept individual fields if provided
		newSky.SkyboxBk = skyasset.Back or newSky.SkyboxBk
		newSky.SkyboxDn = skyasset.Down or newSky.SkyboxDn
		newSky.SkyboxFt = skyasset.Front or newSky.SkyboxFt
		newSky.SkyboxLf = skyasset.Left or newSky.SkyboxLf
		newSky.SkyboxRt = skyasset.Right or newSky.SkyboxRt
		newSky.SkyboxUp = skyasset.Up or newSky.SkyboxUp
	end
	-- remove previous "CoolGuiSky" if exists
	for _, c in pairs(Lighting:GetChildren()) do
		if c.Name == "CoolGuiSky" and c:IsA("Sky") then
			c:Destroy()
		end
	end
	newSky.Parent = Lighting
end

-- Global sound: play a sound under SoundService; this is global server-side sound
local globalSound = nil
local function playGlobalSound(soundId, loop)
	-- expects "rbxassetid://<id>" or numeric id number (string or number)
	if globalSound then
		globalSound:Stop()
		globalSound:Destroy()
		globalSound = nil
	end
	globalSound = Instance.new("Sound")
	globalSound.Name = "CoolGuiGlobalSound"
	if type(soundId) == "number" then
		globalSound.SoundId = "rbxassetid://" .. tostring(soundId)
	else
		globalSound.SoundId = tostring(soundId)
	end
	globalSound.Looped = (loop == true)
	globalSound.Volume = 1
	globalSound.Parent = SoundService
	globalSound:Play()
end

local rainbowRunning = false
local rainbowThread = nil
-- start/stop a simple rainbow cycle that changes colors of parts every 0.5s
local function toggleRainbow(on)
	if on and not rainbowRunning then
		rainbowRunning = true
		captureOriginalParts()
		rainbowThread = coroutine.create(function()
			local hue = 0
			while rainbowRunning do
				hue = (hue + 0.02) % 1
				for _, info in pairs(original.parts) do
					local p = info.Instance
					if p and p.Parent then
						local c = Color3.fromHSV((hue + (p:GetDebugId() % 30)/30) % 1, 0.9, 0.9)
						p.Color = c
					end
				end
				wait(0.5)
			end
		end)
		coroutine.resume(rainbowThread)
	elseif not on and rainbowRunning then
		rainbowRunning = false
		rainbowThread = nil
	end
end

-- Shiny mode: set most parts to Neon (glowy) or revert
local shinyOn = false
local function toggleShiny(on)
	shinyOn = on
	captureOriginalParts()
	for _, info in pairs(original.parts) do
		local p = info.Instance
		if p and p.Parent then
			if on then
				p.Material = Enum.Material.Neon
			else
				p.Material = info.Material or Enum.Material.SmoothPlastic
			end
		end
	end
end

-- Reset everything to the captured originals
local function resetAll()
	-- parts
	for _, info in pairs(original.parts) do
		local p = info.Instance
		if p and p.Parent then
			p.Color = info.Color
			p.Material = info.Material
			p.Transparency = info.Transparency or 0
		end
	end
	-- Lighting restore (some best-effort)
	Lighting.Ambient = original.lighting.Ambient or Color3.fromRGB(128,128,128)
	Lighting.OutdoorAmbient = original.lighting.OutdoorAmbient or Color3.fromRGB(128,128,128)
	if original.lighting.TimeOfDay then
		if Lighting.ClockTime then
			Lighting.ClockTime = original.lighting.TimeOfDay
		else
			Lighting.TimeOfDay = original.lighting.TimeOfDay
		end
	end
	Lighting.Brightness = original.lighting.Brightness or Lighting.Brightness
	-- remove created ColorCorrection/Bloom/Sky
	local cc = Lighting:FindFirstChild("CoolGuiColorCorrection")
	if cc then cc:Destroy() end
	local bloom = Lighting:FindFirstChild("CoolGuiBloom")
	if bloom then bloom:Destroy() end
	local sky = Lighting:FindFirstChild("CoolGuiSky")
	if sky then sky:Destroy() end

	-- stop rainbow and shiny flags
	rainbowRunning = false
	shinyOn = false
	if globalSound then
		globalSound:Stop()
		globalSound:Destroy()
		globalSound = nil
	end
end

-- Handler for client requests (no permissions; anyone can call)
remote.OnServerEvent:Connect(function(player, action, params)
	-- action: string, params: table/primitive
	if action == "MakeEverythingRed" then
		makeEverythingRed()
	elseif action == "SetSkybox" then
		setSkybox(params)
	elseif action == "PlayGlobalSound" then
		playGlobalSound(params and params.id, params and params.loop)
	elseif action == "ToggleRainbow" then
		toggleRainbow(params == true)
	elseif action == "ToggleShiny" then
		toggleShiny(params == true)
	elseif action == "ResetAll" then
		resetAll()
	else
		warn("CoolGuiServer: unknown action", action)
	end
end)

print("[CoolGuiServer] Ready - RemoteEvent:", remote:GetFullName())

