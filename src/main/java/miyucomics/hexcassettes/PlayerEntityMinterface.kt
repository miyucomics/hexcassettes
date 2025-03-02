package miyucomics.hexcassettes

import miyucomics.hexcassettes.data.CassetteState

interface PlayerEntityMinterface {
	fun getCassetteState(): CassetteState
}