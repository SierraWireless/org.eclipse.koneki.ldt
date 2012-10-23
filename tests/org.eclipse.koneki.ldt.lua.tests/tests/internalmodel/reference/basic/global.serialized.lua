do local _ = {
		unknownglobalvars = {
			{
				name = "d",
				shortdescription = "",
				description = "",
				occurrences = {
					{
						sourcerange = {
							min = 1,
							max = 1
						}
						--[[table: 0x83d50d0]],
						definition = nil --[[ref]],
						tag = "MIdentifier"
					}
					--[[table: 0x83c5e30]]
				}
				--[[table: 0x8482130]],
				sourcerange = {
					min = 1,
					max = 1
				}
				--[[table: 0x844c8e8]],
				tag = "item"
			}
			--[[table: 0x844c8c0]]
		}
		--[[table: 0x848d388]],
		content = {
			localvars = {

			}
			--[[table: 0x841e8f0]],
			sourcerange = {
				min = 1,
				max = 10000
			}
			--[[table: 0x841e918]],
			content = {
				nil --[[ref]]
			}
			--[[table: 0x841e8c8]],
			tag = "MBlock"
		}
		--[[table: 0x848d3b0]],
		tag = "MInternalContent"
	}
	--[[table: 0x845b338]];
	_.unknownglobalvars[1].occurrences[1].definition = _.unknownglobalvars[1];
	_.content.content[1] = _.unknownglobalvars[1].occurrences[1];
	return _;
end