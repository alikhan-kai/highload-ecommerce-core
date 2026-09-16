--product key in Redis(for example "product:stock:1")
local stockeyKey = KEYS[1]
--how many pieces does the buyer want to write off 
local amount = tonumber(ARGV[1])

local currentStock = tonumber(redis.call('GET', stockKey) or "0")
if currentStock >= amount then
    local newStock = redis.call('DECRBY', stockKey, amount)
    return newStock
else
    return -1
end