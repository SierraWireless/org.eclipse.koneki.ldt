do local _ = {
		unknownglobalvars = {

		}
		--[[table: 0x8442630]],
		content = {
			localvars = {

			}
			--[[table: 0x83b96d0]],
			sourcerange = {
				min = 1,
				max = 10000
			}
			--[[table: 0x83cc3d8]],
			content = {
				{
					localvars = {
						{
							item = {
								shortdescription = "",
								name = "var",
								occurrences = {
									{
										sourcerange = {
											min = 5,
											max = 7
										}
										--[[table: 0x8508630]],
										definition = nil --[[ref]],
										tag = "MIdentifier"
									}
									--[[table: 0x85085c8]],
									{
										sourcerange = {
											min = 26,
											max = 28
										}
										--[[table: 0x84e74e0]],
										definition = nil --[[ref]],
										tag = "MIdentifier"
									}
									--[[table: 0x84e7478]]
								}
								--[[table: 0x858b918]],
								sourcerange = {
									min = 5,
									max = 7
								}
								--[[table: 0x858b940]],
								description = "",
								tag = "item"
							}
							--[[table: 0x858b8f0]],
							scope = {
								min = 0,
								max = 0
							}
							--[[table: 0x83dc338]]
						}
						--[[table: 0x858b9a8]]
					}
					--[[table: 0x848bf58]],
					sourcerange = {
						min = 1,
						max = 32
					}
					--[[table: 0x84bb520]],
					content = {
						nil --[[ref]],
						nil --[[ref]]
					}
					--[[table: 0x848bf30]],
					tag = "MBlock"
				}
				--[[table: 0x8389fa0]]
			}
			--[[table: 0x83b96a8]],
			tag = "MBlock"
		}
		--[[table: 0x8442658]],
		tag = "MInternalContent"
	}
	--[[table: 0x834a318]];
	_.content.content[1].localvars[1].item.occurrences[1].definition = _.content.content[1].localvars[1].item;
	_.content.content[1].localvars[1].item.occurrences[2].definition = _.content.content[1].localvars[1].item;
	_.content.content[1].content[1] = _.content.content[1].localvars[1].item.occurrences[1];
	_.content.content[1].content[2] = _.content.content[1].localvars[1].item.occurrences[2];
	return _;
end