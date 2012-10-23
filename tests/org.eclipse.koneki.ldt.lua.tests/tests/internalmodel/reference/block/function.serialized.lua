do local _ = {
		unknownglobalvars = {
			{
				name = "f",
				shortdescription = "",
				description = "",
				occurrences = {
					{
						sourcerange = {
							min = 10,
							max = 10
						}
						--[[table: 0x849aa60]],
						definition = nil --[[ref]],
						tag = "MIdentifier"
					}
					--[[table: 0x849aa38]]
				}
				--[[table: 0x84959d8]],
				sourcerange = {
					min = 10,
					max = 10
				}
				--[[table: 0x8495a00]],
				tag = "item"
			}
			--[[table: 0x84958c8]]
		}
		--[[table: 0x83b5da8]],
		content = {
			localvars = {

			}
			--[[table: 0x85505f8]],
			sourcerange = {
				min = 1,
				max = 10000
			}
			--[[table: 0x8550620]],
			content = {
				nil --[[ref]],
				{
					localvars = {

					}
					--[[table: 0x8497590]],
					sourcerange = {
						min = 11,
						max = 17
					}
					--[[table: 0x84975b8]],
					content = {

					}
					--[[table: 0x8497568]],
					tag = "MBlock"
				}
				--[[table: 0x83d41b0]]
			}
			--[[table: 0x85505d0]],
			tag = "MBlock"
		}
		--[[table: 0x83b5dd0]],
		tag = "MInternalContent"
	}
	--[[table: 0x83b5d08]];
	_.unknownglobalvars[1].occurrences[1].definition = _.unknownglobalvars[1];
	_.content.content[1] = _.unknownglobalvars[1].occurrences[1];
	return _;
end