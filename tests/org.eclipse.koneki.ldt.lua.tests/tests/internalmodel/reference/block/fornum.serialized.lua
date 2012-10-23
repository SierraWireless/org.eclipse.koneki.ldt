do local _ = {
		unknownglobalvars = {

		}
		--[[table: 0x846a288]],
		content = {
			localvars = {

			}
			--[[table: 0x846a378]],
			sourcerange = {
				min = 1,
				max = 10000
			}
			--[[table: 0x846a3a0]],
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
										--[[table: 0x8572170]],
										definition = nil --[[ref]],
										tag = "MIdentifier"
									}
									--[[table: 0x8572108]]
								}
								--[[table: 0x8572dd8]],
								sourcerange = {
									min = 5,
									max = 7
								}
								--[[table: 0x8572e00]],
								description = "",
								tag = "item"
							}
							--[[table: 0x8572cc8]],
							scope = {
								min = 0,
								max = 0
							}
							--[[table: 0x8572f08]]
						}
						--[[table: 0x8572ee0]]
					}
					--[[table: 0x846a958]],
					sourcerange = {
						min = 1,
						max = 20
					}
					--[[table: 0x846a980]],
					content = {
						nil --[[ref]]
					}
					--[[table: 0x846a930]],
					tag = "MBlock"
				}
				--[[table: 0x846a890]]
			}
			--[[table: 0x846a350]],
			tag = "MBlock"
		}
		--[[table: 0x846a2b0]],
		tag = "MInternalContent"
	}
	--[[table: 0x846a260]];
	_.content.content[1].localvars[1].item.occurrences[1].definition = _.content.content[1].localvars[1].item;
	_.content.content[1].content[1] = _.content.content[1].localvars[1].item.occurrences[1];
	return _;
end