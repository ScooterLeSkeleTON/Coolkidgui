-- ServerScriptService/CoolGuiServer.lua
-- Handles requests from CoolGui client. Safe, server-authoritative visual effects.

local ReplicatedStorage = game:GetService("ReplicatedStorage")
local Lighting = game:GetService("Lighting")
local SoundService = game:GetService("SoundService")

local REMOTE_NAME = "CoolGuiEvent"

-- Create the RemoteEvent if it doesn't exist
local remote = ReplicatedStorage:FindFirstChild(REMOTE_NAME)
if not remote then
	remote = Instance.new("RemoteEvent")
	remote.Name = REMOTE_NAME
	remote.Parent = ReplicatedStorage
end

-- Utility: safely run a visual effect for everyone
local function setEverythingRed()
	for _, part in ipairs(workspace:GetDescendants()) do
		if part:IsA("BasePart") then
			part.Color = Color3.fromRGB(255, 0, 0)
		elseif part:IsA("Decal") or part:IsA("Texture") then
			part.Color3 = Color3.fromRGB(255, 0, 0)
		end
	end

	Lighting.Ambient = Color3.fromRGB(255, 0, 0)
	Lighting.OutdoorAmbient = Color3.fromRGB(255, 60, 60)
end

local function changeSkybox(skyboxData)
	-- Clear old sky if any
	for _, obj in ipairs(Lighting:GetChildren()) do
		if obj:IsA("Sky") then obj:Destroy() end
	end

	local sky = Instance.new("Sky")
	sky.Name = "CoolGuiSky"
	for k, v in pairs(skyboxData or {}) do
		if sky[k] ~= nil then
			sky[k] = "rbxassetid://" .. tostring(v)
		end
	end
	sky.Parent = Lighting
end

local function playGlobalAudio(params)
	local sound = Instance.new("Sound")
	sound.SoundId = params.SoundId or "rbxassetid://301964312"
	sound.Volume = 3
	sound.Looped = false
	sound.Parent = SoundService
	sound:Play()
	game:GetService("Debris"):AddItem(sound, sound.TimeLength + 2)
end

local function resetWorld()
	for _, part in ipairs(workspace:GetDescendants()) do
		if part:IsA("BasePart") then
			part.Color = Color3.fromRGB(255, 255, 255)
		end
	end
	Lighting.Ambient = Color3.fromRGB(128, 128, 128)
	Lighting.OutdoorAmbient = Color3.fromRGB(128, 128, 128)
end

local darkMode = false
local function toggleDarkMode()
	darkMode = not darkMode
	if darkMode then
		Lighting.Brightness = 0.5
		Lighting.ClockTime = 0
		Lighting.Ambient = Color3.fromRGB(30, 30, 30)
	else
		Lighting.Brightness = 2
		Lighting.ClockTime = 14
		Lighting.Ambient = Color3.fromRGB(128, 128, 128)
	end
end

-- Main event handler
remote.OnServerEvent:Connect(function(player, action, data)
	print(player.Name .. " requested " .. tostring(action))

	if action == "SetEverythingRed" then
		setEverythingRed()
	elseif action == "ChangeSkybox" then
		changeSkybox(data)
	elseif action == "PlayGlobalAudio" then
		playGlobalAudio(data or {})
	elseif action == "ResetWorld" then
		resetWorld()
	elseif action == "ToggleDarkMode" then
		toggleDarkMode()
	end
end)

