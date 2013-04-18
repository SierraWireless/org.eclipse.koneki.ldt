do local _={
		unknownglobalvars={
			[1]={
				description="",
				shortdescription="",
				name="f",
				sourcerange={
					min=1,
					max=1
				}
				--[[table: 005ECEE0]],
				occurrences={
					[1]={
						sourcerange={
							min=1,
							max=1
						}
						--[[table: 005ED5C0]],
						definition=nil --[[ref]],
						tag="MIdentifier"
					}
					--[[table: 005ED548]],
					[2]={
						sourcerange={
							min=14,
							max=14
						}
						--[[table: 005EC2D8]],
						definition=nil --[[ref]],
						tag="MIdentifier"
					}
					--[[table: 005EBD88]]
				}
				--[[table: 005ED728]],
				tag="item"
			}
			--[[table: 005ED750]]
		}
		--[[table: 005D22B8]],
		content={
			localvars={

			}
			--[[table: 005D2330]],
			sourcerange={
				min=1,
				max=10000
			}
			--[[table: 005D2740]],
			content={
				[1]={
					sourcerange={
						min=1,
						max=3
					}
					--[[table: 005EBD38]],
					func=nil --[[ref]],
					tag="MCall"
				}
				--[[table: 005EBB80]],
				[2]=nil --[[ref]],
				[3]={
					localvars={

					}
					--[[table: 005ED098]],
					sourcerange={
						min=15,
						max=21
					}
					--[[table: 005ED0C0]],
					content={

					}
					--[[table: 005ECF58]],
					tag="MBlock"
				}
				--[[table: 005ECA58]]
			}
			--[[table: 005D22E0]],
			tag="MBlock"
		}
		--[[table: 005D2240]],
		tag="MInternalContent"
	}
	--[[table: 005D2268]];
	_.unknownglobalvars[1].occurrences[1].definition=_.unknownglobalvars[1];
	_.unknownglobalvars[1].occurrences[2].definition=_.unknownglobalvars[1];
	_.content.content[1].func=_.unknownglobalvars[1].occurrences[1];
	_.content.content[2]=_.unknownglobalvars[1].occurrences[2];
	return _;
end