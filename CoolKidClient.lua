-- StarterPlayerScripts/CoolGuiClient.lua
-- CoolKid-style client GUI. Sends safe requests to server for global effects.
-- Includes a local-only hover/fly mode (no noclip). Use only in your own place.

local Players = game:GetService("Players")
local ReplicatedStorage = game:GetService("ReplicatedStorage")
local UserInputService = game:GetService("UserInputService")
local RunService = game:GetService("RunService")
local player = Players.LocalPlayer
local PlayerGui = player:WaitForChild("PlayerGui")

local REMOTE_NAME = "CoolGuiEvent"
local remote = ReplicatedStorage:WaitForChild(REMOTE_NAME) -- must exist

-- Utility to create UI
local function createUI()
	local screenGui = Instance.new("ScreenGui")
	screenGui.Name = "CoolGui"
	screenGui.ResetOnSpawn = false
	screenGui.Parent = PlayerGui

	local frame = Instance.new("Frame")
	frame.Name = "Panel"
	frame.Size = UDim2.new(0, 300, 0, 420)
	frame.Position = UDim2.new(0, 12, 0.18, 0)
	frame.BackgroundColor3 = Color3.fromRGB(24, 0, 0)
	frame.BorderSizePixel = 0
	frame.Active = true
	frame.Draggable = true
	frame.Parent = screenGui

	local title = Instance.new("TextLabel")
	title.Size = UDim2.new(1, 0, 0, 36)
	title.BackgroundTransparency = 1
	title.Font = Enum.Font.SourceSansBold
	title.TextSize = 22
	title.Text = "COOL GUI"
	title.TextColor3 = Color3.fromRGB(255, 160, 160)
	title.Parent = frame

	local function makeButton(text, y, callback)
		local b = Instance.new("TextButton")
		b.Size = UDim2.new(1, -20, 0, 36)
		b.Position = UDim2.new(0, 10, 0, y)
		b.BackgroundColor3 = Color3.fromRGB(160, 0, 0)
		b.TextColor3 = Color3.new(1, 1, 1)
		b.Font = Enum.Font.SourceSansBold
		b.TextSize = 18
		b.Text = text
		b.Parent = frame
		b.MouseButton1Click:Connect(function()
			pcall(callback)
		end)
		return b
	end

	-- Buttons that ask the server to perform global effects
	makeButton("Turn Everything Red", 56, function()
		remote:FireServer("SetEverythingRed")
	end)

	makeButton("Change Skybox", 100, function()
		-- sends optional skyAssetIds table to server. Server should handle applying.
		-- Replace with your own skybox asset IDs or send nil to use server defaults.
		local skyboxData = {
			-- example placeholders: replace these numbers with your asset ids
			SkyboxBK = 123456789,
			SkyboxDN = 123456789,
			SkyboxFT = 123456789,
			SkyboxLF = 123456789,
			SkyboxRT = 123456789,
			SkyboxUP = 123456789,
		}
		remote:FireServer("ChangeSkybox", skyboxData)
	end)

	makeButton("Play Global Audio", 144, function()
		-- Replace soundId with your own asset id (SoundService:PlayLocalSound or server spawns sound)
		local params = { SoundId = "rbxassetid://301964312" } -- placeholder
		remote:FireServer("PlayGlobalAudio", params)
	end)

	-- Local-only hover/fly toggle button (client-side)
	local flyBtn = makeButton("Toggle Hover/Fly (Local)", 188, function()
		-- toggling handled below via exposed toggle function
		toggleFly()
	end)

	makeButton("Reset World", 232, function()
		remote:FireServer("ResetWorld")
	end)

	-- Additional fun effects (client only visual toggles)
	local rainbowBtn = makeButton("Toggle Local Rainbow (Parts)", 276, function()
		toggleLocalRainbow()
	end)

	makeButton("Toggle Dark Mode (Request)", 320, function()
		remote:FireServer("ToggleDarkMode")
	end)

	-- simple footer
	local footer = Instance.new("TextLabel")
	footer.Size = UDim2.new(1, -20, 0, 32)
	footer.Position = UDim2.new(0, 10, 1, -40)
	footer.BackgroundTransparency = 1
	footer.Font = Enum.Font.SourceSans
	footer.TextSize = 14
	footer.Text = "Client-side controls + server requests"
	footer.TextColor3 = Color3.fromRGB(200, 200, 200)
	footer.Parent = frame
end

-- ================================
-- Local-only Hover/Fly Implementation
-- This is CLIENT-SIDE ONLY. It does not modify collisions or server physics.
-- It uses a BodyVelocity to gently move the player while enabled.
-- ================================

local flyEnabled = false
local bodyVelocity
local flySpeed = 60 -- movement multiplier
local ascendSpeed = 60 -- upward speed when holding jump
local descendSpeed = 60 -- downward when holding crouch (LeftControl)
local connection

function toggleFly()
	-- safety: ensure character exists
	local char = player.Character or player.CharacterAdded:Wait()
	local hrp = char:FindFirstChild("HumanoidRootPart")
	local hum = char:FindFirstChildOfClass("Humanoid")
	if not hrp or not hum then
		return
	end

	flyEnabled = not flyEnabled

	if flyEnabled then
		-- create BodyVelocity (client-owned)
		bodyVelocity = Instance.new("BodyVelocity")
		bodyVelocity.Name = "CoolGuiFlyBV"
		bodyVelocity.MaxForce = Vector3.new(1e5, 1e5, 1e5)
		bodyVelocity.Velocity = Vector3.new(0,0,0)
		bodyVelocity.P = 1250
		bodyVelocity.Parent = hrp

		-- optional small BodyGyro to stabilize orientation (client-side)
		local bodyGyro = Instance.new("BodyGyro")
		bodyGyro.Name = "CoolGuiFlyBG"
		bodyGyro.MaxTorque = Vector3.new(4e4, 4e4, 4e4)
		bodyGyro.P = 5000
		bodyGyro.CFrame = hrp.CFrame
		bodyGyro.Parent = hrp

		-- prevent Humanoid auto state death of animations interfering
		hum.PlatformStand = false

		-- update loop to handle controls
		connection = RunService.RenderStepped:Connect(function(dt)
			if not flyEnabled or not hrp or not hrp.Parent then return end

			-- gather input axes
			local moveDir = Vector3.new(0,0,0)
			-- WASD / arrow keys
			local camera = workspace.CurrentCamera
			local forward = camera.CFrame.LookVector
			local right = camera.CFrame.RightVector

			if UserInputService:IsKeyDown(Enum.KeyCode.W) then moveDir = moveDir + Vector3.new(forward.X, 0, forward.Z) end
			if UserInputService:IsKeyDown(Enum.KeyCode.S) then moveDir = moveDir - Vector3.new(forward.X, 0, forward.Z) end
			if UserInputService:IsKeyDown(Enum.KeyCode.A) then moveDir = moveDir - Vector3.new(right.X, 0, right.Z) end
			if UserInputService:IsKeyDown(Enum.KeyCode.D) then moveDir = moveDir + Vector3.new(right.X, 0, right.Z) end

			-- vertical control: Space = ascend, LeftControl = descend
			local vertical = 0
			if UserInputService:IsKeyDown(Enum.KeyCode.Space) then vertical = vertical + 1 end
			if UserInputService:IsKeyDown(Enum.KeyCode.LeftControl) then vertical = vertical - 1 end

			-- normalized movement
			if moveDir.Magnitude > 0 then
				moveDir = moveDir.Unit
			end

			-- compute target velocity
			local targetVel = (moveDir * flySpeed) + Vector3.new(0, vertical * ascendSpeed, 0)
			-- smoothly approach target
			local current = bodyVelocity.Velocity
			local lerpFactor = math.clamp(8 * dt, 0, 1)
			bodyVelocity.Velocity = current:Lerp(targetVel, lerpFactor)
		end)
	else
		-- disable fly: cleanup
		if connection then
			connection:Disconnect()
			connection = nil
		end
		if bodyVelocity and bodyVelocity.Parent then
			bodyVelocity:Destroy()
		end
		-- remove BodyGyro if present
		local char2 = player.Character
		if char2 and char2:FindFirstChild("HumanoidRootPart") then
			local hrp2 = char2.HumanoidRootPart
			local bg = hrp2:FindFirstChild("CoolGuiFlyBG")
			if bg then bg:Destroy() end
		end
		-- reset humanoid state
		local hum2 = char and char:FindFirstChildOfClass("Humanoid")
		if hum2 then hum2.PlatformStand = false end
	end
end

-- ================================
-- Local Rainbow Visual (client-only)
-- This simply tints nearby parts the client can see by applying a transient local CollectionService highlight or changing LocalTransparencyModifier.
-- NOTE: This is purely client-side visual and does not replicate to others.
-- We'll implement a per-frame color pulse on parts within a radius.
-- ================================
local rainbowEnabled = false
local rainbowConnection
local function toggleLocalRainbow()
	rainbowEnabled = not rainbowEnabled
	if rainbowEnabled then
		local hue = 0
		rainbowConnection = RunService.RenderStepped:Connect(function(dt)
			hue = (hue + dt * 0.12) % 1
			-- find nearby parts (simple: workspace:GetDescendants but keep light)
			for _, obj in ipairs(workspace:GetDescendants()) do
				if obj:IsA("BasePart") and obj.Parent and obj.Transparency < 1 then
					-- apply local color change by altering LocalTransparencyModifier and using a Highlight isn't supported universally,
					-- so we simply change the local player's Camera:GetPartsInRadius if you want more complex visuals on client.
					-- To avoid messing with server state, do nothing server-side here.
					-- For clarity: this example will not modify part.Color to avoid replicating changes.
				end
			end
			-- A safer client-only visual is to add a ScreenGui color overlay cycling hues:
			-- (we'll update a small overlay below instead of altering parts)
			-- handled by overlay code created once
			if screenOverlay then
				local rgb = Color3.fromHSV(hue, 0.6, 0.9)
				screenOverlay.BackgroundColor3 = rgb
				screenOverlay.BackgroundTransparency = 0.85
			end
		end)
	else
		if rainbowConnection then
			rainbowConnection:Disconnect()
			rainbowConnection = nil
		end
		if screenOverlay then
			screenOverlay:Destroy()
			screenOverlay = nil
		end
	end
end

-- small subtle screen overlay for client-only rainbow effect
local screenOverlay
local function createOverlay()
	if screenOverlay and screenOverlay.Parent then return end
	local gui = PlayerGui:FindFirstChild("CoolGui")
	if not gui then return end
	local overlay = Instance.new("Frame")
	overlay.Name = "RainbowOverlay"
	overlay.Size = UDim2.new(1,0,1,0)
	overlay.Position = UDim2.new(0,0,0,0)
	overlay.BackgroundTransparency = 1
	overlay.BorderSizePixel = 0
	overlay.Parent = gui
	screenOverlay = overlay
end

-- call createUI and set up overlay hook
createUI()
createOverlay()

-- expose toggleFly and toggleLocalRainbow to the UI scope (make local functions reachable)
-- Note: the makeButton callback references toggleFly/toggleLocalRainbow which are defined above.

-- Clean up when character respawns to avoid orphaned BodyVelocity
player.CharacterAdded:Connect(function(char)
	-- ensure fly is disabled on respawn for safety
	if flyEnabled then
		toggleFly()
	end
end)

-- End of CoolGuiClient.lua

