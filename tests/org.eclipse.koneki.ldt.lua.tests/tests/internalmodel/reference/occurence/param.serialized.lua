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
				--[[table: 008893C8]],
				occurrences={
					[1]={
						sourcerange={
							min=1,
							max=1
						}
						--[[table: 008860B0]],
						definition=nil --[[ref]],
						tag="MIdentifier"
					}
					--[[table: 00886218]],
					[2]={
						sourcerange={
							min=14,
							max=14
						}
						--[[table: 008929C8]],
						definition=nil --[[ref]],
						tag="MIdentifier"
					}
					--[[table: 0088CD20]]
				}
				--[[table: 00890D08]],
				tag="item"
			}
			--[[table: 008914D8]]
		}
		--[[table: 00892680]],
		content={
			localvars={

			}
			--[[table: 0088F890]],
			sourcerange={
				min=1,
				max=10000
			}
			--[[table: 0088E738]],
			content={
				[1]={
					sourcerange={
						min=1,
						max=3
					}
					--[[table: 00887ED8]],
					func=nil --[[ref]],
					tag="MCall"
				}
				--[[table: 00886D30]],
				[2]=nil --[[ref]],
				[3]={
					localvars={
						[1]={
							item={
								description="",
								shortdescription="",
								name="param",
								sourcerange={
									min=17,
									max=21
								}
								--[[table: 00889170]],
								occurrences={
									[1]={
										sourcerange={
											min=17,
											max=21
										}
										--[[table: 00889A80]],
										definition=nil --[[ref]],
										tag="MIdentifier"
									}
									--[[table: 00889A08]],
									[2]={
										sourcerange={
											min=33,
											max=37
										}
										--[[table: 00893058]],
										definition=nil --[[ref]],
										tag="MIdentifier"
									}
									--[[table: 00892EC8]]
								}
								--[[table: 00889148]],
								tag="item"
							}
							--[[table: 00891488]],
							scope={
								min=0,
								max=0
							}
							--[[table: 0088B2E0]]
						}
						--[[table: 00887C08]]
					}
					--[[table: 0088C780]],
					sourcerange={
						min=16,
						max=41
					}
					--[[table: 00890790]],
					content={
						[1]=nil --[[ref]],
						[2]=nil --[[ref]]
					}
					--[[table: 0088CCA8]],
					tag="MBlock"
				}
				--[[table: 0088A098]]
			}
			--[[table: 0088F958]],
			tag="MBlock"
		}
		--[[table: 0088ED28]],
		tag="MInternalContent"
	}
	--[[table: 00893328]];
	_.unknownglobalvars[1].occurrences[1].definition=_.unknownglobalvars[1];
	_.unknownglobalvars[1].occurrences[2].definition=_.unknownglobalvars[1];
	_.content.content[1].func=_.unknownglobalvars[1].occurrences[1];
	_.content.content[2]=_.unknownglobalvars[1].occurrences[2];
	_.content.content[3].localvars[1].item.occurrences[1].definition=_.content.content[3].localvars[1].item;
	_.content.content[3].localvars[1].item.occurrences[2].definition=_.content.content[3].localvars[1].item;
	_.content.content[3].content[1]=_.content.content[3].localvars[1].item.occurrences[1];
	_.content.content[3].content[2]=_.content.content[3].localvars[1].item.occurrences[2];
	return _;
end