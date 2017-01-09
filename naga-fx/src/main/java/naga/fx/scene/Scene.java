package naga.fx.scene;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.Property;
import javafx.beans.property.ReadOnlyProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.ObservableList;
import naga.commons.scheduler.Scheduled;
import naga.commons.scheduler.UiScheduler;
import naga.commons.util.Strings;
import naga.commons.util.collection.Collections;
import naga.fx.event.EventTarget;
import naga.fx.geometry.Orientation;
import naga.fx.naga.tk.ScenePeer;
import naga.fx.naga.tk.StagePeer;
import naga.fx.properties.ObservableLists;
import naga.fx.properties.markers.HasHeightProperty;
import naga.fx.properties.markers.HasRootProperty;
import naga.fx.properties.markers.HasWidthProperty;
import naga.fx.scene.control.Button;
import naga.fx.scene.control.Skin;
import naga.fx.scene.control.Skinnable;
import naga.fx.spi.Toolkit;
import naga.fx.spi.viewer.NodeViewer;
import naga.fx.stage.Window;
import naga.fx.sun.event.EventDispatchChain;
import naga.fx.sun.event.EventDispatcher;
import naga.fx.sun.scene.SceneEventDispatcher;
import naga.fx.sun.tk.TKPulseListener;
import naga.fx.sun.tk.TKSceneListener;

import java.util.Collection;

/**
 * @author Bruno Salmon
 */
public class Scene implements EventTarget,
        HasRootProperty,
        HasWidthProperty,
        HasHeightProperty {

    private double widthSetByUser = -1.0;
    private double heightSetByUser = -1.0;
    private boolean sizeInitialized = false;

    public Scene(Parent root) {
        this(root, -1, -1);
    }

    public Scene(Parent root, double width, double height) {
        setRoot(root);
        init(width, height);
    }

    private void init(double width, double height) {
        if (width >= 0) {
            widthSetByUser = width;
            setWidth(width);
        }
        if (height >= 0) {
            heightSetByUser = height;
            setHeight(height);
        }
        sizeInitialized = (widthSetByUser >= 0 && heightSetByUser >= 0);
    }

    private final Property<Double> widthProperty = new SimpleObjectProperty<Double>(0d) {
        @Override
        protected void invalidated() {
            Parent _root = getRoot();
            if (_root.isResizable()) {
                resizeRootOnSceneSizeChange(get() - _root.getLayoutX() /*- _root.getTranslateX()*/, _root.getLayoutBounds().getHeight());
            }
        }
    };

    @Override
    public Property<Double> widthProperty() {
        return widthProperty;
    }

    private final Property<Double> heightProperty = new SimpleObjectProperty<Double>(0d) {
        @Override
        protected void invalidated() {
            Parent _root = getRoot();
            if (_root.isResizable()) {
                resizeRootOnSceneSizeChange(_root.getLayoutBounds().getWidth(), get() - _root.getLayoutY() /*- _root.getTranslateY()*/);
            }
        }
    };
    @Override
    public Property<Double> heightProperty() {
        return heightProperty;
    }

    void resizeRootOnSceneSizeChange(double newWidth, double newHeight) {
        getRoot().resize(newWidth, newHeight);
    }

    private final Property<Parent> rootProperty = new SimpleObjectProperty<Parent>() {
        // Temporary code to automatically assume the following behaviour:
        // - the root node width is bound to the scene width
        // - the scene height is bound to the root node height (which eventually is bound to the preferred height)
        @Override
        protected void invalidated() {
            Parent root = getValue();
            root.setScene(Scene.this);
            root.setSceneRoot(true);
            createAndBindRootNodeViewerAndChildren(getRoot());
        }
    };

    /**
     * The horizontal location of this {@code Scene} on the {@code Window}.
     */
    private Property<Double> x;

    private final void setX(double value) {
        xPropertyImpl().setValue(value);
    }

    public final double getX() {
        return x == null ? 0.0 : x.getValue();
    }

    public final ReadOnlyProperty<Double> xProperty() {
        return xPropertyImpl()/*.getReadOnlyProperty()*/;
    }

    private Property<Double> xPropertyImpl() {
        if (x == null) {
            x = new SimpleObjectProperty<>(this, "x", 0d);
        }
        return x;
    }

    /**
     * The vertical location of this {@code Scene} on the {@code Window}.
     */
    private Property<Double> y;

    private void setY(double value) {
        yPropertyImpl().setValue(value);
    }

    public final double getY() {
        return y == null ? 0.0 : y.getValue();
    }

    public final ReadOnlyProperty<Double> yProperty() {
        return yPropertyImpl()/*.getReadOnlyProperty()*/;
    }

    private Property<Double> yPropertyImpl() {
        if (y == null) {
            y = new SimpleObjectProperty<>(this, "y", 0d);
        }
        return y;
    }

    @Override
    public Property<Parent> rootProperty() {
        return rootProperty;
    }

    /**
     * The {@code Window} for this {@code Scene}
     */
    private Property<Window> window;

    private void setWindow(Window value) {
        windowPropertyImpl().setValue(value);
    }

    public final Window getWindow() {
        return window == null ? null : window.getValue();
    }

    public final ReadOnlyProperty<Window> windowProperty() {
        return windowPropertyImpl()/*.getReadOnlyProperty()*/;
    }

    private Property<Window> windowPropertyImpl() {
        if (window == null) {
            window = new SimpleObjectProperty<Window>() {
                private Window oldWindow;

                @Override protected void invalidated() {
                    Window newWindow = getValue();
                    ///getKeyHandler().windowForSceneChanged(oldWindow, newWindow);
                    if (oldWindow != null) {
                        impl_disposePeer();
                    }
                    if (newWindow != null) {
                        impl_initPeer();
                    }
                    //parentEffectiveOrientationInvalidated();

                    oldWindow = newWindow;
                }

                @Override
                public Object getBean() {
                    return Scene.this;
                }

                @Override
                public String getName() {
                    return "window";
                }
            };
        }
        return window;
    }

    //@Deprecated
    public void impl_setWindow(Window value) {
        setWindow(value);
    }


    private void preferredSize() {
        final Parent root = getRoot();

        // one or the other isn't initialized, need to perform layout in
        // order to ensure we can properly measure the preferred size of the
        // scene
        doCSSPass();

        resizeRootToPreferredSize(root);
        doLayoutPass();

        if (widthSetByUser < 0) {
            setWidth(root.isResizable()? root.getLayoutX() /*+ root.getTranslateX()*/ + root.getLayoutBounds().getWidth() :
                    root.getLayoutBounds().getMaxX());
        } else {
            setWidth(widthSetByUser);
        }

        if (heightSetByUser < 0) {
            setHeight(root.isResizable()? root.getLayoutY() /*+ root.getTranslateY()*/ + root.getLayoutBounds().getHeight() :
                    root.getLayoutBounds().getMaxY());
        } else {
            setHeight(heightSetByUser);
        }

        sizeInitialized = (getWidth() > 0) && (getHeight() > 0);

        //PerformanceTracker.logEvent("Scene preferred bounds computation complete");
    }

    final void resizeRootToPreferredSize(Parent root) {
        final double preferredWidth;
        final double preferredHeight;

        final Orientation contentBias = root.getContentBias();
        if (contentBias == null) {
            preferredWidth = getPreferredWidth(root, widthSetByUser, -1);
            preferredHeight = getPreferredHeight(root, heightSetByUser, -1);
        } else if (contentBias == Orientation.HORIZONTAL) {
            // height depends on width
            preferredWidth = getPreferredWidth(root, widthSetByUser, -1);
            preferredHeight = getPreferredHeight(root, heightSetByUser,
                    preferredWidth);
        } else /* if (contentBias == Orientation.VERTICAL) */ {
            // width depends on height
            preferredHeight = getPreferredHeight(root, heightSetByUser, -1);
            preferredWidth = getPreferredWidth(root, widthSetByUser,
                    preferredHeight);
        }

        root.resize(preferredWidth, preferredHeight);
    }

    private static double getPreferredWidth(Parent root,
                                            double forcedWidth,
                                            double height) {
        if (forcedWidth >= 0) {
            return forcedWidth;
        }
        final double normalizedHeight = (height >= 0) ? height : -1;
        return root.boundedSize(root.prefWidth(normalizedHeight),
                root.minWidth(normalizedHeight),
                root.maxWidth(normalizedHeight));
    }

    private static double getPreferredHeight(Parent root,
                                             double forcedHeight,
                                             double width) {
        if (forcedHeight >= 0) {
            return forcedHeight;
        }
        final double normalizedWidth = (width >= 0) ? width : -1;
        return root.boundedSize(root.prefHeight(normalizedWidth),
                root.minHeight(normalizedWidth),
                root.maxHeight(normalizedWidth));
    }

    /**
     * @treatAsPrivate implementation detail
     */
    //@Deprecated
    public void impl_preferredSize() {
        preferredSize();
    }

    /***************************************************************************
     *                                                                         *
     *                         Event Dispatch                                  *
     *                                                                         *
     **************************************************************************/
    // PENDING_DOC_REVIEW
    /**
     * Specifies the event dispatcher for this scene. When replacing the value
     * with a new {@code EventDispatcher}, the new dispatcher should forward
     * events to the replaced dispatcher to keep the scene's default event
     * handling behavior.
     */
    private ObjectProperty<EventDispatcher> eventDispatcher;

    public final void setEventDispatcher(EventDispatcher value) {
        eventDispatcherProperty().set(value);
    }

    public final EventDispatcher getEventDispatcher() {
        return eventDispatcherProperty().get();
    }

    public final ObjectProperty<EventDispatcher> eventDispatcherProperty() {
        initializeInternalEventDispatcher();
        return eventDispatcher;
    }

    private SceneEventDispatcher internalEventDispatcher;


    final void initializeInternalEventDispatcher() {
        if (internalEventDispatcher == null) {
            internalEventDispatcher = createInternalEventDispatcher();
            eventDispatcher = new SimpleObjectProperty<>(
                    this,
                    "eventDispatcher",
                    internalEventDispatcher);
        }
    }

    private SceneEventDispatcher createInternalEventDispatcher() {
        return new SceneEventDispatcher(this);
    }

    /**
     * Construct an event dispatch chain for this scene. The event dispatch
     * chain contains all event dispatchers from the stage to this scene.
     *
     * @param tail the initial chain to build from
     * @return the resulting event dispatch chain for this scene
     */
    @Override
    public EventDispatchChain buildEventDispatchChain(EventDispatchChain tail) {
        if (eventDispatcher != null) {
            final EventDispatcher eventDispatcherValue = eventDispatcher.get();
            if (eventDispatcherValue != null) {
                tail = tail.prepend(eventDispatcherValue);
            }
        }

        if (getWindow() != null) {
            tail = getWindow().buildEventDispatchChain(tail);
        }

        return tail;
    }


    private final SceneRequester sceneRequester = new SceneRequester() {

        @Override
        public void requestNodeViewerPropertyUpdate(Node node, ObservableValue changedProperty) {
            execute(() -> updateViewProperty(node, changedProperty));
        }

        @Override
        public void requestNodeViewerListUpdate(Node node, ObservableList changedList) {
            execute(() -> updateViewList(node, changedList));
        }

        private void execute(Runnable runnable) {
            UiScheduler uiScheduler = Toolkit.get().scheduler();
            if (uiScheduler.isAnimationFrame())
                runnable.run();
            else
                uiScheduler.scheduleAnimationFrame(runnable);
        }
    };

    public SceneRequester getSceneRequester() {
        return sceneRequester;
    }

    private void keepParentAndChildrenViewersUpdated(Parent parent) {
        ObservableLists.runNowAndOnListChange(() -> {
            // Setting the parent to all children
            for (Node child : parent.getChildren())
                child.setParent(parent);
            updateParentAndChildrenViewers(parent);
        }, parent.getChildren());
    }

    private void updateParentAndChildrenViewers(Parent parent) {
        impl_getPeer().updateParentAndChildrenViewers(parent);
    }

    private boolean updateViewProperty(Node node, ObservableValue changedProperty) {
        boolean hitChangedProperty = updateViewProperty(getOrCreateAndBindNodeViewer(node), changedProperty);
        if (hitChangedProperty || changedProperty == null)
            impl_getPeer().onPropertyHit();
        return hitChangedProperty;
    }

    private boolean updateViewProperty(NodeViewer nodeViewer, ObservableValue changedProperty) {
        return nodeViewer.updateProperty(changedProperty);
    }

    private boolean updateViewList(Node node, ObservableList changedList) {
        return updateViewList(getOrCreateAndBindNodeViewer(node), changedList);
    }

    private boolean updateViewList(NodeViewer nodeViewer, ObservableList changedList) {
        return nodeViewer.updateList(changedList);
    }

    public void updateChildrenViewers(Collection<Node> nodes) {
        Collections.forEach(nodes, this::createAndBindNodeViewerAndChildren);
    }

    private void createAndBindRootNodeViewerAndChildren(Node rootNode) {
        createAndBindNodeViewerAndChildren(rootNode);
        impl_getPeer().onRootBound();
    }

    private void createAndBindNodeViewerAndChildren(Node node) {
        NodeViewer nodeViewer = getOrCreateAndBindNodeViewer(node);
        if (nodeViewer instanceof Parent)
            updateChildrenViewers(((Parent) nodeViewer).getChildren());
    }

    public NodeViewer getOrCreateAndBindNodeViewer(Node node) {
        if (node.getScene() != this)
            node.setScene(this);
        NodeViewer nodeViewer = node.getNodeViewer();
        if (nodeViewer == null) {
            node.setNodeViewer(nodeViewer = createNodeViewer(node));
            if (nodeViewer == null) // The node view factory was unable to create a view for this node!
                node.setNodeViewer(nodeViewer = createUnimplementedNodeViewer(node)); // Displaying a "Unimplemented..." button instead
            else { // Standard case (the node view was successfully created)
                nodeViewer.bind(node, sceneRequester);
                if (node instanceof Parent) {
                    Parent parent = (Parent) node;
                    if (parent instanceof Skinnable) {
                        Skinnable skinnable = (Skinnable) parent;
                        if (skinnable.getSkin() == null) {
                            skinnable.skinProperty().addListener(new ChangeListener<Skin<?>>() {
                                @Override
                                public void changed(ObservableValue<? extends Skin<?>> observable, Skin<?> oldValue, Skin<?> newValue) {
                                    observable.removeListener(this);
                                    keepParentAndChildrenViewersUpdated(parent);
                                }
                            });
                            return nodeViewer;
                        }
                    }
                    keepParentAndChildrenViewersUpdated(parent);
                }
            }
        }
        return nodeViewer;
    }


    private NodeViewer<Node> createNodeViewer(Node node) {
        ScenePeer peer = impl_getPeer();
        NodeViewer<Node> nodeViewer = peer.getNodeViewerFactory().createNodeViewer(node);
        peer.onNodeViewerCreated(nodeViewer);
        return nodeViewer;
    }

    private NodeViewer createUnimplementedNodeViewer(Node node) {
        // Creating a button as replacement (assuming the target toolkit at least implements a button view!)
        Button button = new Button(Strings.removeSuffix(node.getClass().getSimpleName(), "Impl") + " viewer not provided");
        // Binding to allow the button to respond to the original node layout
        button.layoutXProperty().bind(node.layoutXProperty());
        button.layoutYProperty().bind(node.layoutYProperty());
        if (node instanceof HasWidthProperty)
            button.widthProperty().bind(((HasWidthProperty) node).widthProperty());
        if (node instanceof HasHeightProperty)
            button.heightProperty().bind(((HasHeightProperty) node).heightProperty());
        return getOrCreateAndBindNodeViewer(button); // Finally retuning the button view
    }

    private Scheduled pulseScheduled;

    public boolean isPulseRunning() {
        return pulseScheduled != null;
    }

    public void startPulse() {
        if (pulseScheduled == null)
            pulseScheduled = Toolkit.get().scheduler().schedulePeriodicAnimationFrame(scenePulseListener::pulse, true);
    }

    public void stopPulse() {
        if (pulseScheduled != null) {
            pulseScheduled.cancel();
            pulseScheduled = null;
        }
    }

    private void doCSSPass() {
    }

    private void doLayoutPass() {
        Parent root = getRoot();
        if (root != null)
            root.layout();
    }

    /**
     * The peer of this scene
     *
     * @treatAsPrivate implementation detail
     */
    //@Deprecated
    private ScenePeer impl_peer;

    /**
     * Get Scene's peer
     *
     * @treatAsPrivate implementation detail
     */
    //@Deprecated
    public ScenePeer impl_getPeer() {
        if (impl_peer == null)
            impl_peer = Toolkit.get().createScenePeer(this);
        return impl_peer;
    }

    /**
     * @treatAsPrivate implementation detail
     */
    //@Deprecated
    public void impl_initPeer() {
        //assert impl_peer == null;

        Window window = getWindow();
        // impl_initPeer() is only called from Window, either when the window
        // is being shown, or the window scene is being changed. In any case
        // this scene's window cannot be null.
        assert window != null;

        StagePeer windowPeer = window.impl_getPeer();
        if (windowPeer == null) {
            // This is fine, the window is not visible. impl_initPeer() will
            // be called again later, when the window is being shown.
            return;
        }

/*
        final boolean isTransparentWindowsSupported = Platform.isSupported(ConditionalFeature.TRANSPARENT_WINDOW);
        if (!isTransparentWindowsSupported) {
            PlatformImpl.addNoTransparencyStylesheetToScene(this);
        }

        PerformanceTracker.logEvent("Scene.initPeer started");

        impl_setAllowPGAccess(true);

        Toolkit tk = Toolkit.getToolkit();
*/
        //impl_peer = windowPeer.createTKScene(isDepthBufferInternal(), getAntiAliasingInternal(), acc);
        //PerformanceTracker.logEvent("Scene.initPeer TKScene created");
        impl_peer.setTKSceneListener(new ScenePeerListener());
        //impl_peer.setTKScenePaintListener(new ScenePeerPaintListener());
        //PerformanceTracker.logEvent("Scene.initPeer TKScene set");
        //impl_peer.setRoot(getRoot().impl_getPeer());
        //impl_peer.setFillPaint(getFill() == null ? null : tk.getPaint(getFill()));
        //getEffectiveCamera().impl_updatePeer();
        //impl_peer.setCamera((NGCamera) getEffectiveCamera().impl_getPeer());
        //impl_peer.markDirty();
        //PerformanceTracker.logEvent("Scene.initPeer TKScene initialized");

        //impl_setAllowPGAccess(false);

        if (getRoot() != null)
            getRoot().requestLayout();
        startPulse(); // tk.addSceneTkPulseListener(scenePulseListener);
/*
        // listen to dnd gestures coming from the platform
        if (PLATFORM_DRAG_GESTURE_INITIATION) {
            if (dragGestureListener == null) {
                dragGestureListener = new DragGestureListener();
            }
            tk.registerDragGestureListener(impl_peer, EnumSet.allOf(TransferMode.class), dragGestureListener);
        }
        tk.enableDrop(impl_peer, new DropTargetListener());
        tk.installInputMethodRequests(impl_peer, new InputMethodRequestsDelegate());

        PerformanceTracker.logEvent("Scene.initPeer finished");
*/
    }

    /**
     * @treatAsPrivate implementation detail
     */
    //@Deprecated
    public void impl_disposePeer() {
        if (impl_peer == null) {
            // This is fine, the window is either not shown yet and there is no
            // need in disposing scene peer, or is hidden and impl_disposePeer()
            // has already been called.
            return;
        }

/*
        PerformanceTracker.logEvent("Scene.disposePeer started");

        Toolkit tk = Toolkit.getToolkit();
*/
        stopPulse(); //tk.removeSceneTkPulseListener(scenePulseListener);
/*
        if (accessible != null) {
            disposeAccessibles();
            Node root = getRoot();
            if (root != null) root.releaseAccessible();
            accessible.dispose();
            accessible = null;
        }
        impl_peer.dispose();
        impl_peer = null;

        PerformanceTracker.logEvent("Scene.disposePeer finished");
*/
    }


    /**
     * The scene pulse listener that gets called on toolkit pulses
     */
    ScenePulseListener scenePulseListener = new ScenePulseListener();

    /*******************************************************************************
     *                                                                             *
     * Scene Pulse Listener                                                        *
     *                                                                             *
     ******************************************************************************/

    class ScenePulseListener implements TKPulseListener {

        //private boolean firstPulse = true;

        /**
         * PG synchronizer. Called once per frame from the pulse listener.
         * This function calls the synchronizePGNode method on each node in
         * the dirty list.
         */
/*
        private void synchronizeSceneNodes() {
            Toolkit.getToolkit().checkFxUserThread();

            Scene.inSynchronizer = true;

            // if dirtyNodes is null then that means this Scene has not yet been
            // synchronized, and so we will simply synchronize every node in the
            // scene and then create the dirty nodes array list
            if (Scene.this.dirtyNodes == null) {
                // must do this recursively
                syncAll(getRoot());
                dirtyNodes = new Node[MIN_DIRTY_CAPACITY];

            } else {
                // This is not the first time this scene has been synchronized,
                // so we will only synchronize those nodes that need it
                for (int i = 0 ; i < dirtyNodesSize; ++i) {
                    Node node = dirtyNodes[i];
                    dirtyNodes[i] = null;
                    if (node.getScene() == Scene.this) {
                        node.impl_syncPeer();
                    }
                }
                dirtyNodesSize = 0;
            }

            Scene.inSynchronizer = false;
        }
*/

        /**
         * Recursive function for synchronizing every node in the scenegraph.
         * The return value is the number of nodes in the graph.
         */
/*
        private int syncAll(Node node) {
            node.impl_syncPeer();
            int size = 1;
            if (node instanceof Parent) {
                Parent p = (Parent) node;
                final int childrenCount = p.getChildren().size();

                for (int i = 0; i < childrenCount; i++) {
                    Node n = p.getChildren().get(i);
                    if (n != null) {
                        size += syncAll(n);
                    }
                }
            } else if (node instanceof SubScene) {
                SubScene subScene = (SubScene)node;
                size += syncAll(subScene.getRoot());
            }
            if (node.getClip() != null) {
                size += syncAll(node.getClip());
            }

            return size;
        }
*/

/*
        private void synchronizeSceneProperties() {
            inSynchronizer = true;
            if (isDirty(DirtyBits.ROOT_DIRTY)) {
                impl_peer.setRoot(getRoot().impl_getPeer());
            }

            if (isDirty(DirtyBits.FILL_DIRTY)) {
                Toolkit tk = Toolkit.getToolkit();
                impl_peer.setFillPaint(getFill() == null ? null : tk.getPaint(getFill()));
            }

            // new camera was set on the scene or old camera changed
            final Camera cam = getEffectiveCamera();
            if (isDirty(DirtyBits.CAMERA_DIRTY)) {
                cam.impl_updatePeer();
                impl_peer.setCamera((NGCamera) cam.impl_getPeer());
            }

            if (isDirty(DirtyBits.CURSOR_DIRTY)) {
                mouseHandler.updateCursor(getCursor());
            }

            clearDirty();
            inSynchronizer = false;
        }
*/

        /**
         * The focus is considered dirty if something happened to
         * the scene graph that may require the focus to be moved.
         * This must handle cases where (a) the focus owner may have
         * become ineligible to have the focus, and (b) where the focus
         * owner is null and a node may have become traversable and eligible.
         */
/*
        private void focusCleanup() {
            if (Scene.this.isFocusDirty()) {
                final Node oldOwner = Scene.this.getFocusOwner();
                if (oldOwner == null) {
                    Scene.this.focusInitial();
                } else if (oldOwner.getScene() != Scene.this) {
                    Scene.this.requestFocus(null);
                    Scene.this.focusInitial();
                } else if (!oldOwner.isCanReceiveFocus()) {
                    Scene.this.requestFocus(null);
                    Scene.this.focusIneligible(oldOwner);
                }
                Scene.this.setFocusDirty(false);
            }
        }
*/

        @Override
        public void pulse() {
            impl_getPeer().onBeforePulse();

/*
            if (Scene.this.tracker != null) {
                Scene.this.tracker.pulse();
            }
            if (firstPulse) {
                PerformanceTracker.logEvent("Scene - first repaint");
            }

            focusCleanup();

            disposeAccessibles();

            if (PULSE_LOGGING_ENABLED) {
                PulseLogger.newPhase("CSS Pass");
            }
*/
            Scene.this.doCSSPass();

/*
            if (PULSE_LOGGING_ENABLED) {
                PulseLogger.newPhase("Layout Pass");
            }
*/
            Scene.this.doLayoutPass();

/*
            boolean dirty = dirtyNodes == null || dirtyNodesSize != 0 || !isDirtyEmpty();
            if (dirty) {
                if (PULSE_LOGGING_ENABLED) {
                    PulseLogger.newPhase("Update bounds");
                }
                getRoot().updateBounds();
                if (impl_peer != null) {
                    try {
                        if (PULSE_LOGGING_ENABLED) {
                            PulseLogger.newPhase("Waiting for previous rendering");
                        }
                        impl_peer.waitForRenderingToComplete();
                        impl_peer.waitForSynchronization();
                        // synchronize scene properties
                        if (PULSE_LOGGING_ENABLED) {
                            PulseLogger.newPhase("Copy state to render graph");
                        }
                        syncLights();
                        synchronizeSceneProperties();
                        // Run the synchronizer
                        synchronizeSceneNodes();
                        Scene.this.mouseHandler.pulse();
                        // Tell the scene peer that it needs to repaint
                        impl_peer.markDirty();
                    } finally {
                        impl_peer.releaseSynchronization(true);
                    }
                } else {
                    if (PULSE_LOGGING_ENABLED) {
                        PulseLogger.newPhase("Synchronize with null peer");
                    }
                    synchronizeSceneNodes();
                    Scene.this.mouseHandler.pulse();
                }

                if (Scene.this.getRoot().cssFlag != CssFlags.CLEAN) {
                    Scene.this.getRoot().impl_markDirty(com.sun.javafx.scene.DirtyBits.NODE_CSS);
                }
            }
*/

/*
            // required for image cursor created from animated image
            Scene.this.mouseHandler.updateCursorFrame();

            if (firstPulse) {
                if (PerformanceTracker.isLoggingEnabled()) {
                    PerformanceTracker.logEvent("Scene - first repaint - layout complete");
                    if (PrismSettings.perfLogFirstPaintFlush) {
                        PerformanceTracker.outputLog();
                    }
                    if (PrismSettings.perfLogFirstPaintExit) {
                        System.exit(0);
                    }
                }
                firstPulse = false;
            }

            if (testPulseListener != null) {
                testPulseListener.run();
            }
*/
            impl_getPeer().onAfterPulse();
        }
    }


    /*******************************************************************************
     *                                                                             *
     * Scene Peer Listener                                                         *
     *                                                                             *
     ******************************************************************************/

    class ScenePeerListener implements TKSceneListener {
        @Override
        public void changedLocation(float x, float y) {
            if (x != Scene.this.getX()) {
                Scene.this.setX(x);
            }
            if (y != Scene.this.getY()) {
                Scene.this.setY(y);
            }
        }

        @Override
        public void changedSize(float w, float h) {
            if (w != Scene.this.getWidth()) Scene.this.setWidth((double)w);
            if (h != Scene.this.getHeight()) Scene.this.setHeight((double)h);
        }

/*
        @Override
        public void mouseEvent(EventType<MouseEvent> type, double x, double y, double screenX, double screenY,
                               MouseButton button, boolean popupTrigger, boolean synthesized,
                               boolean shiftDown, boolean controlDown, boolean altDown, boolean metaDown,
                               boolean primaryDown, boolean middleDown, boolean secondaryDown)
        {
            MouseEvent mouseEvent = new MouseEvent(type, x, y, screenX, screenY, button,
                    0, // click count will be adjusted by clickGenerator later anyway
                    shiftDown, controlDown, altDown, metaDown,
                    primaryDown, middleDown, secondaryDown, synthesized, popupTrigger, false, null);
            impl_processMouseEvent(mouseEvent);
        }

        @Override
        public void keyEvent(KeyEvent keyEvent)
        {
            impl_processKeyEvent(keyEvent);
        }

        @Override
        public void inputMethodEvent(EventType<InputMethodEvent> type,
                                     ObservableList<InputMethodTextRun> composed, String committed,
                                     int caretPosition)
        {
            InputMethodEvent inputMethodEvent = new InputMethodEvent(
                    type, composed, committed, caretPosition);
            processInputMethodEvent(inputMethodEvent);
        }

        public void menuEvent(double x, double y, double xAbs, double yAbs,
                              boolean isKeyboardTrigger) {
            Scene.this.processMenuEvent(x, y, xAbs,yAbs, isKeyboardTrigger);
        }

        @Override
        public void scrollEvent(
                EventType<ScrollEvent> eventType,
                double scrollX, double scrollY,
                double totalScrollX, double totalScrollY,
                double xMultiplier, double yMultiplier,
                int touchCount,
                int scrollTextX, int scrollTextY,
                int defaultTextX, int defaultTextY,
                double x, double y, double screenX, double screenY,
                boolean _shiftDown, boolean _controlDown,
                boolean _altDown, boolean _metaDown,
                boolean _direct, boolean _inertia) {

            ScrollEvent.HorizontalTextScrollUnits xUnits = scrollTextX > 0 ?
                    ScrollEvent.HorizontalTextScrollUnits.CHARACTERS :
                    ScrollEvent.HorizontalTextScrollUnits.NONE;

            double xText = scrollTextX < 0 ? 0 : scrollTextX * scrollX;

            ScrollEvent.VerticalTextScrollUnits yUnits = scrollTextY > 0 ?
                    ScrollEvent.VerticalTextScrollUnits.LINES :
                    (scrollTextY < 0 ?
                            ScrollEvent.VerticalTextScrollUnits.PAGES :
                            ScrollEvent.VerticalTextScrollUnits.NONE);

            double yText = scrollTextY < 0 ? scrollY : scrollTextY * scrollY;

            xMultiplier = defaultTextX > 0 && scrollTextX >= 0
                    ? Math.round(xMultiplier * scrollTextX / defaultTextX)
                    : xMultiplier;

            yMultiplier = defaultTextY > 0 && scrollTextY >= 0
                    ? Math.round(yMultiplier * scrollTextY / defaultTextY)
                    : yMultiplier;

            if (eventType == ScrollEvent.SCROLL_FINISHED) {
                x = scrollGesture.sceneCoords.getX();
                y = scrollGesture.sceneCoords.getY();
                screenX = scrollGesture.screenCoords.getX();
                screenY = scrollGesture.screenCoords.getY();
            } else if (Double.isNaN(x) || Double.isNaN(y) ||
                    Double.isNaN(screenX) || Double.isNaN(screenY)) {
                if (cursorScenePos == null || cursorScreenPos == null) {
                    return;
                }
                x = cursorScenePos.getX();
                y = cursorScenePos.getY();
                screenX = cursorScreenPos.getX();
                screenY = cursorScreenPos.getY();
            }

            inMousePick = true;
            Scene.this.processGestureEvent(new ScrollEvent(
                            eventType,
                            x, y, screenX, screenY,
                            _shiftDown, _controlDown, _altDown, _metaDown,
                            _direct, _inertia,
                            scrollX * xMultiplier, scrollY * yMultiplier,
                            totalScrollX * xMultiplier, totalScrollY * yMultiplier,
                            xMultiplier, yMultiplier,
                            xUnits, xText, yUnits, yText, touchCount, pick(x, y)),
                    scrollGesture);
            inMousePick = false;
        }

        @Override
        public void zoomEvent(
                EventType<ZoomEvent> eventType,
                double zoomFactor, double totalZoomFactor,
                double x, double y, double screenX, double screenY,
                boolean _shiftDown, boolean _controlDown,
                boolean _altDown, boolean _metaDown,
                boolean _direct, boolean _inertia) {

            if (eventType == ZoomEvent.ZOOM_FINISHED) {
                x = zoomGesture.sceneCoords.getX();
                y = zoomGesture.sceneCoords.getY();
                screenX = zoomGesture.screenCoords.getX();
                screenY = zoomGesture.screenCoords.getY();
            } else if (Double.isNaN(x) || Double.isNaN(y) ||
                    Double.isNaN(screenX) || Double.isNaN(screenY)) {
                if (cursorScenePos == null || cursorScreenPos == null) {
                    return;
                }
                x = cursorScenePos.getX();
                y = cursorScenePos.getY();
                screenX = cursorScreenPos.getX();
                screenY = cursorScreenPos.getY();
            }

            inMousePick = true;
            Scene.this.processGestureEvent(new ZoomEvent(eventType,
                            x, y, screenX, screenY,
                            _shiftDown, _controlDown, _altDown, _metaDown,
                            _direct, _inertia,
                            zoomFactor, totalZoomFactor, pick(x, y)),
                    zoomGesture);
            inMousePick = false;
        }

        @Override
        public void rotateEvent(
                EventType<RotateEvent> eventType, double angle, double totalAngle,
                double x, double y, double screenX, double screenY,
                boolean _shiftDown, boolean _controlDown,
                boolean _altDown, boolean _metaDown,
                boolean _direct, boolean _inertia) {

            if (eventType == RotateEvent.ROTATION_FINISHED) {
                x = rotateGesture.sceneCoords.getX();
                y = rotateGesture.sceneCoords.getY();
                screenX = rotateGesture.screenCoords.getX();
                screenY = rotateGesture.screenCoords.getY();
            } else if (Double.isNaN(x) || Double.isNaN(y) ||
                    Double.isNaN(screenX) || Double.isNaN(screenY)) {
                if (cursorScenePos == null || cursorScreenPos == null) {
                    return;
                }
                x = cursorScenePos.getX();
                y = cursorScenePos.getY();
                screenX = cursorScreenPos.getX();
                screenY = cursorScreenPos.getY();
            }

            inMousePick = true;
            Scene.this.processGestureEvent(new RotateEvent(
                            eventType, x, y, screenX, screenY,
                            _shiftDown, _controlDown, _altDown, _metaDown,
                            _direct, _inertia, angle, totalAngle, pick(x, y)),
                    rotateGesture);
            inMousePick = false;

        }

        @Override
        public void swipeEvent(
                EventType<SwipeEvent> eventType, int touchCount,
                double x, double y, double screenX, double screenY,
                boolean _shiftDown, boolean _controlDown,
                boolean _altDown, boolean _metaDown, boolean _direct) {

            if (Double.isNaN(x) || Double.isNaN(y) ||
                    Double.isNaN(screenX) || Double.isNaN(screenY)) {
                if (cursorScenePos == null || cursorScreenPos == null) {
                    return;
                }
                x = cursorScenePos.getX();
                y = cursorScenePos.getY();
                screenX = cursorScreenPos.getX();
                screenY = cursorScreenPos.getY();
            }

            inMousePick = true;
            Scene.this.processGestureEvent(new SwipeEvent(
                            eventType, x, y, screenX, screenY,
                            _shiftDown, _controlDown, _altDown, _metaDown, _direct,
                            touchCount, pick(x, y)),
                    swipeGesture);
            inMousePick = false;
        }

        @Override
        public void touchEventBegin(
                long time, int touchCount, boolean isDirect,
                boolean _shiftDown, boolean _controlDown,
                boolean _altDown, boolean _metaDown) {

            if (!isDirect) {
                nextTouchEvent = null;
                return;
            }
            nextTouchEvent = new TouchEvent(
                    TouchEvent.ANY, null, null, 0,
                    _shiftDown, _controlDown, _altDown, _metaDown);
            if (touchPoints == null || touchPoints.length != touchCount) {
                touchPoints = new TouchPoint[touchCount];
            }
            touchPointIndex = 0;
        }

        @Override
        public void touchEventNext(
                TouchPoint.State state, long touchId,
                double x, double y, double screenX, double screenY) {

            inMousePick = true;
            if (nextTouchEvent == null) {
                // ignore indirect touch events
                return;
            }
            touchPointIndex++;
            int id = (state == TouchPoint.State.PRESSED
                    ? touchMap.add(touchId) :  touchMap.get(touchId));
            if (state == TouchPoint.State.RELEASED) {
                touchMap.remove(touchId);
            }
            int order = touchMap.getOrder(id);

            if (order >= touchPoints.length) {
                throw new RuntimeException("Too many touch points reported");
            }

            // pick target
            boolean isGrabbed = false;
            PickResult pickRes = pick(x, y);
            EventTarget pickedTarget = touchTargets.get(id);
            if (pickedTarget == null) {
                pickedTarget = pickRes.getIntersectedNode();
                if (pickedTarget == null) {
                    pickedTarget = Scene.this;
                }
            } else {
                isGrabbed = true;
            }

            TouchPoint tp = new TouchPoint(id, state,
                    x, y, screenX, screenY, pickedTarget, pickRes);

            touchPoints[order] = tp;

            if (isGrabbed) {
                tp.grab(pickedTarget);
            }
            if (tp.getState() == TouchPoint.State.PRESSED) {
                tp.grab(pickedTarget);
                touchTargets.put(tp.getId(), pickedTarget);
            } else if (tp.getState() == TouchPoint.State.RELEASED) {
                touchTargets.remove(tp.getId());
            }
            inMousePick = false;
        }

        @Override
        public void touchEventEnd() {
            if (nextTouchEvent == null) {
                // ignore indirect touch events
                return;
            }

            if (touchPointIndex != touchPoints.length) {
                throw new RuntimeException("Wrong number of touch points reported");
            }

            Scene.this.processTouchEvent(nextTouchEvent, touchPoints);

            if (touchMap.cleanup()) {
                // gesture finished
                touchEventSetId = 0;
            }
        }

        @Override
        public Accessible getSceneAccessible() {
            return getAccessible();
        }
*/
    }


}