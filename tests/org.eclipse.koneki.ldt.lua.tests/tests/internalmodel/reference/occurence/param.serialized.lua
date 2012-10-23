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
						--[[table: 0x8554b60]],
						definition = nil --[[ref]],
						tag = "MIdentifier"
					}
					--[[table: 0x845cfd8]]
				}
				--[[table: 0x8550fe0]],
				sourcerange = {
					min = 10,
					max = 10
				}
				--[[table: 0x8551008]],
				tag = "item"
			}
			--[[table: 0x850d288]]
		}
		--[[table: 0x844d648]],
		content = {
			localvars = {

			}
			--[[table: 0x844d738]],
			sourcerange = {
				min = 1,
				max = 10000
			}
			--[[table: 0x8432c98]],
			content = {
				nil --[[ref]],
				{
					localvars = {
						{
							item = {
								shortdescription = "",
								name = "param",
								occurrences = {
									{
										sourcerange = {
											min = 13,
											max = 17
										}
										--[[table: 0x853ab78]],
										definition = nil --[[ref]],
										tag = "MIdentifier"
									}
									--[[table: 0x8554ae8]],
									{
										sourcerange = {
											min = 29,
											max = 33
										}
										--[[table: 0x848b600]],
										definition = nil --[[ref]],
										tag = "MIdentifier"
									}
									--[[table: 0x848b5d8]]
								}
								--[[table: 0x83d69b8]],
								sourcerange = {
									min = 13,
									max = 17
								}
								--[[table: 0x83d69e0]],
								description = "",
								tag = "item"
							}
							--[[table: 0x850d260]],
							scope = {
								min = 0,
								max = 0
							}
							--[[table: 0x83d6b80]]
						}
						--[[table: 0x83d6b58]]
					}
					--[[table: 0x850e8a0]],
					sourcerange = {
						min = 12,
						max = 37
					}
					--[[table: 0x850e8c8]],
					content = {
						nil --[[ref]],
						nil --[[ref]]
					}
					--[[table: 0x850e878]],
					tag = "MBlock"
				}
				--[[table: 0x850e7d8]]
			}
			--[[table: 0x844d710]],
			tag = "MBlock"
		}
		--[[table: 0x844d670]],
		tag = "MInternalContent"
	}
	--[[table: 0x844d5a8]];
	_.unknownglobalvars[1].occurrences[1].definition = _.unknownglobalvars[1];
	_.content.content[1] = _.unknownglobalvars[1].occurrences[1];
	_.content.content[2].localvars[1].item.occurrences[1].definition = _.content.content[2].localvars[1].item;
	_.content.content[2].localvars[1].item.occurrences[2].definition = _.content.content[2].localvars[1].item;
	_.content.content[2].content[1] = _.content.content[2].localvars[1].item.occurrences[1];
	_.content.content[2].content[2] = _.content.content[2].localvars[1].item.occurrences[2];
	return _;
end