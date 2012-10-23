do local _={
		unknownglobalvars={
			[1]={
				name="d",
				shortdescription="",
				description="",
				occurrences={
					[1]={
						sourcerange={
							min=27,
							max=27
						}
						--[[table: 0x84e1858]],
						definition=nil --[[ref]],
						tag="MIdentifier"
					}
					--[[table: 0x84e1830]]
				}
				--[[table: 0x84e3470]],
				sourcerange={
					min=27,
					max=27
				}
				--[[table: 0x84e3498]],
				tag="item"
			}
			--[[table: 0x84e3360]],
			[2]={
				name="f",
				shortdescription="",
				description="",
				occurrences={
					[1]={
						sourcerange={
							min=10,
							max=10
						}
						--[[table: 0x84e0870]],
						definition=nil --[[ref]],
						tag="MIdentifier"
					}
					--[[table: 0x84e0848]]
				}
				--[[table: 0x84e3688]],
				sourcerange={
					min=10,
					max=10
				}
				--[[table: 0x84e36b0]],
				tag="item"
			}
			--[[table: 0x84e3578]]
		}
		--[[table: 0x84dfef0]],
		content={
			localvars={

			}
			--[[table: 0x84dffe0]],
			sourcerange={
				min=1,
				max=10000
			}
			--[[table: 0x84e0008]],
			content={
				[1]=nil --[[ref]],
				[2]={
					localvars={
						[1]={
							item={
								shortdescription="",
								name="d",
								occurrences={
									[1]={
										sourcerange={
											min=21,
											max=21
										}
										--[[table: 0x84e14b8]],
										definition=nil --[[ref]],
										tag="MIdentifier"
									}
									--[[table: 0x84e1450]]
								}
								--[[table: 0x84e31c8]],
								sourcerange={
									min=21,
									max=21
								}
								--[[table: 0x84e31f0]],
								description="",
								tag="item"
							}
							--[[table: 0x84e30b8]],
							scope={
								min=0,
								max=0
							}
							--[[table: 0x84e32f8]]
						}
						--[[table: 0x84e32d0]]
					}
					--[[table: 0x84e0c78]],
					sourcerange={
						min=11,
						max=25
					}
					--[[table: 0x84e0ca0]],
					content={
						[1]=nil --[[ref]]
					}
					--[[table: 0x84e0c50]],
					tag="MBlock"
				}
				--[[table: 0x84e0bb0]],
				[3]=nil --[[ref]]
			}
			--[[table: 0x84dffb8]],
			tag="MBlock"
		}
		--[[table: 0x84dff18]],
		tag="MInternalContent"
	}
	--[[table: 0x84dfe50]];
	_.unknownglobalvars[1].occurrences[1].definition=_.unknownglobalvars[1];
	_.unknownglobalvars[2].occurrences[1].definition=_.unknownglobalvars[2];
	_.content.content[1]=_.unknownglobalvars[2].occurrences[1];
	_.content.content[2].localvars[1].item.occurrences[1].definition=_.content.content[2].localvars[1].item;
	_.content.content[2].content[1]=_.content.content[2].localvars[1].item.occurrences[1];
	_.content.content[3]=_.unknownglobalvars[1].occurrences[1];
	return _;
end
