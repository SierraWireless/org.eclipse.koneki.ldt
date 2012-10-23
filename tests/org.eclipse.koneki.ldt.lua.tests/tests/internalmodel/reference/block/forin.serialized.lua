do local _ = {
		unknownglobalvars = {
			{
				name = "lists",
				shortdescription = "",
				description = "",
				occurrences = {
					{
						sourcerange = {
							min = 12,
							max = 16
						}
						--[[table: 0x83c0d58]],
						definition = nil --[[ref]],
						tag = "MIdentifier"
					}
					--[[table: 0x83b2700]]
				}
				--[[table: 0x84fc290]],
				sourcerange = {
					min = 12,
					max = 16
				}
				--[[table: 0x852f288]],
				tag = "item"
			}
			--[[table: 0x8539bb0]]
		}
		--[[table: 0x8456f70]],
		content = {
			localvars = {

			}
			--[[table: 0x8574ba0]],
			sourcerange = {
				min = 1,
				max = 10000
			}
			--[[table: 0x84ef818]],
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
										--[[table: 0x84764e8]],
										definition = nil --[[ref]],
										tag = "MIdentifier"
									}
									--[[table: 0x856ee00]]
								}
								--[[table: 0x853d400]],
								sourcerange = {
									min = 5,
									max = 7
								}
								--[[table: 0x84919e8]],
								description = "",
								tag = "item"
							}
							--[[table: 0x8389b08]],
							scope = {
								min = 0,
								max = 0
							}
							--[[table: 0x83e5328]]
						}
						--[[table: 0x8565d60]]
					}
					--[[table: 0x85630b8]],
					sourcerange = {
						min = 1,
						max = 24
					}
					--[[table: 0x837fdf8]],
					content = {
						nil --[[ref]],
						nil --[[ref]]
					}
					--[[table: 0x857a530]],
					tag = "MBlock"
				}
				--[[table: 0x84023a8]]
			}
			--[[table: 0x857bdd8]],
			tag = "MBlock"
		}
		--[[table: 0x83f3978]],
		tag = "MInternalContent"
	}
	--[[table: 0x83d8968]];
	_.unknownglobalvars[1].occurrences[1].definition = _.unknownglobalvars[1];
	_.content.content[1].localvars[1].item.occurrences[1].definition = _.content.content[1].localvars[1].item;
	_.content.content[1].content[1] = _.unknownglobalvars[1].occurrences[1];
	_.content.content[1].content[2] = _.content.content[1].localvars[1].item.occurrences[1];
	return _;
end