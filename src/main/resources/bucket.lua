local key = KEYS[1]
local capacity = tonumber(ARGV[1])
local refillRate = tonumber(ARGV[2])
local now = tonumber(ARGV[3])
local requestedTokens = tonumber(ARGV[4])

local data = redis.call('GET', key)
if not data then
    redis.call('SET', key, capacity - requestedTokens .. ',' .. now, 'EX', 60)
    return 1
end

local tokens, lastRefill = string.match(data, '(%d+),(%d+)')
tokens = tonumber(tokens)
lastRefill = tonumber(lastRefill)

local elapsedTime = now - lastRefill
local newTokens = math.min(capacity, tokens + (elapsedTime * refillRate))

if newTokens < requestedTokens then
    return 0
end

redis.call('SET', key, newTokens - requestedTokens .. ',' .. now, 'EX', 60)
return 1