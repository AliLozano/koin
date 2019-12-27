package org.koin.core.state

import kotlinx.cinterop.StableRef
import kotlin.native.concurrent.ensureNeverFrozen
import kotlin.native.concurrent.freeze

internal actual class MainIsolatedState<T:Any> actual constructor(startVal: T) {
    private val stableRef = StableRef.create(startVal)
    init {
        startVal.ensureNeverFrozen()
        freeze()
    }
    actual fun _get(): T = stableRef.get()
}