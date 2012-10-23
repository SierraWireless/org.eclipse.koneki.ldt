local toto
for var =1, 0 do
    var = 2
end 

for _ in toto() do
    var()
    var = var() 
end
