do local _ = {
		unknownglobalvars = {
			{
				name = "list",
				shortdescription = "",
				description = "",
				occurrences = {
					{
						sourcerange = {
							min = 12,
							max = 15
						}
						--[[table: 0x842da60]],
						definition = nil --[[ref]],
						tag = "MIdentifier"
					}
					--[[table: 0x8404550]]
				}
				--[[table: 0x8499490]],
				sourcerange = {
					min = 12,
					max = 15
				}
				--[[table: 0x84994b8]],
				tag = "item"
			}
			--[[table: 0x8499380]]
		}
		--[[table: 0x842d7f0]],
		content = {
			localvars = {

			}
			--[[table: 0x842d8e0]],
			sourcerange = {
				min = 1,
				max = 10000
			}
			--[[table: 0x842d908]],
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
										--[[table: 0x84dfec0]],
										definition = nil --[[ref]],
										tag = "MIdentifier"
									}
									--[[table: 0x84dfe58]],
									{
										sourcerange = {
											min = 29,
											max = 31
										}
										--[[table: 0x84e03f8]],
										definition = nil --[[ref]],
										tag = "MIdentifier"
									}
									--[[table: 0x84e03d0]]
								}
								--[[table: 0x8499180]],
								sourcerange = {
									min = 5,
									max = 7
								}
								--[[table: 0x84991a8]],
								description = "",
								tag = "item"
							}
							--[[table: 0x8499070]],
							scope = {
								min = 0,
								max = 0
							}
							--[[table: 0x8499358]]
						}
						--[[table: 0x8499330]]
					}
					--[[table: 0x8401980]],
					sourcerange = {
						min = 1,
						max = 35
					}
					--[[table: 0x84019a8]],
					content = {
						nil --[[ref]],
						nil --[[ref]],
						nil --[[ref]]
					}
					--[[table: 0x8401958]],
					tag = "MBlock"
				}
				--[[table: 0x84018b8]]
			}
			--[[table: 0x842d8b8]],
			tag = "MBlock"
		}
		--[[table: 0x842d818]],
		tag = "MInternalContent"
	}
	--[[table: 0x842d750]];
	_.unknownglobalvars[1].occurrences[1].definition = _.unknownglobalvars[1];
	_.content.content[1].localvars[1].item.occurrences[1].definition = _.content.content[1].localvars[1].item;
	_.content.content[1].localvars[1].item.occurrences[2].definition = _.content.content[1].localvars[1].item;
	_.content.content[1].content[1] = _.unknownglobalvars[1].occurrences[1];
	_.content.content[1].content[2] = _.content.content[1].localvars[1].item.occurrences[1];
	_.content.content[1].content[3] = _.content.content[1].localvars[1].item.occurrences[2];
	return _;
end