package naga.fx.spi.peer.base;

import naga.fx.scene.control.ButtonBase;

/**
 * @author Bruno Salmon
 */
public interface ButtonBasePeerMixin
        <N extends ButtonBase, NB extends ButtonBasePeerBase<N, NB, NM>, NM extends ButtonBasePeerMixin<N, NB, NM>>

        extends LabeledPeerMixin<N, NB, NM> {
}