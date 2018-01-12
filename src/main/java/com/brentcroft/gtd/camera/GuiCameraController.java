package com.brentcroft.gtd.camera;

import com.brentcroft.gtd.adapter.model.GuiObject;
import com.brentcroft.gtd.adapter.model.GuiObjectVisitor;
import com.brentcroft.gtd.driver.Backend;
import com.brentcroft.gtd.driver.LocatorException;
import com.brentcroft.gtd.utilities.CanonicalPath;
import com.brentcroft.gtd.utilities.DocumentNotificationBuffer;
import com.brentcroft.gtd.utilities.EventNotification;
import com.brentcroft.gtd.utilities.HashCache;
import com.brentcroft.gtd.utilities.events.JMXNotifier;
import com.brentcroft.util.DateUtils;
import com.brentcroft.util.Waiter8;
import com.brentcroft.util.XmlUtils;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.stream.Collectors;
import javafx.application.Platform;
import javafx.event.Event;
import javax.management.Notification;
import javax.management.NotificationBroadcasterSupport;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.w3c.dom.Element;

import static com.brentcroft.gtd.driver.Backend.XML_NAMESPACE_TAG;
import static com.brentcroft.gtd.driver.Backend.XML_NAMESPACE_URI;
import static java.lang.String.format;

public class GuiCameraController extends NotificationBroadcasterSupport implements GuiCameraControllerMBean, JMXNotifier
{
    private final static transient Log logger = LogFactory.getLog( GuiCameraController.class );

    private Camera camera;

    private boolean isShuttingDown = false;

    private long notificationSequenceNumber = 0;

    private EventNotification eventNotification;


    public GuiCameraController( Camera camera )
    {
        this.camera = camera;
        this.eventNotification = new EventNotification( camera, this );
    }

    public synchronized Object shutdown( final int status )
    {
        if ( isShuttingDown )
        {
            return "Already shutting down.";
        }

        logger.info( format( "Shutting down controller: status=[%s].", status ) );

        isShuttingDown = true;

        long millisUntilShutdown = 1000;

        eventNotification.shutdownNow( millisUntilShutdown );

        try
        {
            camera.shutdown();
        }
        catch ( Exception e )
        {
            logger.warn( "Error calling camera.shutdown()", e );
        }

        try
        {
            DocumentNotificationBuffer.shutdownNow();
        }
        catch ( Exception e )
        {
            logger.warn( "Error calling DocumentNotificationBuffer shutdown", e );
        }

        Platform.exit();

        // this is not kind to the harness

        try
        {
            return format( "Calling System exit: status=[%s].",
                    status,
                    millisUntilShutdown );
        }
        finally
        {
            System.exit( status );
        }
    }


    /**
     * Simply echos the object back.
     * <p/>
     * <p>
     * Used to test if the bean is operable.
     */
    @Override
    public Object echo( Object o )
    {
        return o;
    }


    public String getSnapshotXmlText()
    {
        return getSnapshotXmlText( null );
    }


    @Override
    public String getSnapshotXmlText( Map< String, Object > options )
    {
        return XmlUtils.serialize( camera.takeSnapshot( options ) );
    }


    @Override
    public String getSnapshotXmlText( String path, Map< String, Object > options )
    {
        // immediate - no poll or delay
        GuiObject guiObject = getGuiObject( path, 0, 0 );

        long started = System.currentTimeMillis();

        // a client snapshot
        Element element = XmlUtils.parse( format( "<snapshot xmlns:%s=\"%s\"/>",
                XML_NAMESPACE_TAG,
                XML_NAMESPACE_URI ) ).getDocumentElement();


        guiObject.accept( new GuiObjectVisitor( element, options ) );


        long finished = System.currentTimeMillis();
        long duration = ( finished - started );

        element.setAttribute( "duration", "" + duration );
        XmlUtils.maybeSetElementAttribute( element, null, "options", options );
        element.setAttribute( "timestamp", DateUtils.timestamp() );
        element.setAttribute( "xpath", path );


        return XmlUtils.serialize( element );
    }


    public GuiObject getGuiObject( final String path, double timeout, double pollInterval )
    {
        CanonicalPath cp = CanonicalPath.newCanonicalPath( path );

        HashCache< GuiObject > hc = camera.getObjectManager().getHashCache();

        if ( cp.hasHash() && hc.isEnabled() && hc.hasCachedObject( cp.getHash() ) )
        {
            // lookup in hash cache
            return hc.getCachedObject( cp.getHash() );
        }

        return camera.getGuiObject(
                pollInterval,
                timeout,
                cp.getXPath() );
    }

    //@Override
    public Object[] getGuiObjects( double timeout, double pollInterval, final String... path )
    {
        List< String > xpaths = Arrays.asList( path )
                .stream()
                .map( p -> CanonicalPath.newCanonicalPath( p ).getXPath() )
                .collect( Collectors.toList() );

        return camera.getGuiObjects(
                pollInterval,
                timeout,
                xpaths.toArray( new String[ xpaths.size() ] ) );
    }

    public boolean exists( String path, double timeout, double pollInterval )
    {
        try
        {
            return null != getGuiObject( path, timeout, pollInterval );
        }
        catch ( LocatorException e )
        {
            if ( logger.isDebugEnabled() )
            {
                logger.debug( e.getMessage() );
            }

            return false;
        }
    }

    public boolean[] existsAll( double timeout, double pollInterval, String... paths )
    {
        if ( paths == null )
        {
            return null;
        }

        try
        {
            Object[] guiObjects = getGuiObjects( pollInterval, timeout, paths );

            boolean[] b = new boolean[ paths.length ];

            for ( int i = 0, n = paths.length; i < n; i++ )
            {
                b[ i ] = ( guiObjects[ i ] != null );
            }

            return b;
        }
        catch ( LocatorException e )
        {
            if ( logger.isDebugEnabled() )
            {
                logger.debug( e.getMessage() );
            }
        }
        return null;
    }


    public boolean notExists( final String path, double timeout, double pollInterval )
    {
        try
        {
            new Waiter8()
                    .until( () -> null == camera.getObjectAtPath( path ) )
                    .onTimeout( millis -> {
                        throw new LocatorException(
                                format( "Object still exists at path [%s] after [%d] millis", path, millis ) );
                    } )
                    .withTimeoutMillis( DateUtils.secondsToMillis( timeout ) )
                    .withDelayMillis( DateUtils.secondsToMillis( pollInterval ) )
                    .start();

            return true;
        }
        catch ( LocatorException e )
        {
            if ( logger.isDebugEnabled() )
            {
                logger.debug( e.getMessage() );
            }

            return false;
        }
    }


    public void setText( final String path, final String text, double timeout, double pollInterval )
    {
        getGuiObject( path, timeout, pollInterval )
                .asText()
                .setText( text );

    }

    public String getText( final String path, double timeout, double pollInterval )
    {
        return getGuiObject( path, timeout, pollInterval )
                .asText()
                .getText();
    }

    public void click( final String path, double timeout, double pollInterval )
    {
        getGuiObject( path, timeout, pollInterval )
                .asClick()
                .click();
    }

    public void robotClick( final String path, double timeout, double pollInterval )
    {
        getGuiObject( path, timeout, pollInterval )
                .asRobot()
                .robotClick();
    }

    public void robotDoubleClick( final String path, double timeoutSeconds, double pollIntervalSeconds )
    {
        getGuiObject( path, timeoutSeconds, pollIntervalSeconds )
                .asRobot()
                .robotDoubleClick();
    }

    public void robotKeys( final String path, final String keys, double timeoutSeconds, double pollIntervalSeconds )
    {
        getGuiObject( path, timeoutSeconds, pollIntervalSeconds )
                .asRobot()
                .robotKeys( keys );
    }

    @Override
    public void robotClickPoint( String path, int x, int y, double timeoutSeconds, double pollIntervalSeconds )
    {
        getGuiObject( path, timeoutSeconds, pollIntervalSeconds )
                .asRobot()
                .robotClick();
    }

    @Override
    public void robotDoubleClickPoint( String path, int x, int y, double timeoutSeconds, double pollIntervalSeconds )
    {
        getGuiObject( path, timeoutSeconds, pollIntervalSeconds )
                .asRobot()
                .robotDoubleClick();
    }

    @Override
    public void robotKeysPoint( String path, String keys, int x, int y, double timeoutSeconds, double pollIntervalSeconds )
    {
        getGuiObject( path, timeoutSeconds, pollIntervalSeconds )
                .asRobot()
                .robotKeys( keys );
    }


    public void selectTreeNode( String path, String treePathText, double timeout, double pollInterval )
    {
        getGuiObject( path, timeout, pollInterval )
                .asTree()
                .selectPath( treePathText );
    }

    public void selectTableRow( String path, int row, double timeout, double pollInterval )
    {
        getGuiObject( path, timeout, pollInterval )
                .asTable()
                .selectRow( row );
    }

    public void selectTableColumn( String path, int column, double timeout, double pollInterval )
    {
        getGuiObject( path, timeout, pollInterval )
                .asTable()
                .selectColumn( column );
    }

    public void selectTableCell( String path, int row, int column, double timeout, double pollInterval )
    {
        getGuiObject( path, timeout, pollInterval )
                .asTable()
                .selectCell( row, column );
    }

    public void setSelectedIndex( String path, int index, double timeout, double pollInterval )
    {
        getGuiObject( path, timeout, pollInterval )
                .asIndex()
                .setSelectedIndex( index );
    }

    public Integer getSelectedIndex( String path, double timeout, double pollInterval )
    {
        return getGuiObject( path, timeout, pollInterval )
                .asIndex()
                .getSelectedIndex();
    }

    public Integer getItemCount( String path, double timeout, double pollInterval )
    {
        return getGuiObject( path, timeout, pollInterval )
                .asIndex()
                .getItemCount();
    }


    public void execute( String path, final String script, double timeout, double pollInterval )
    {
//        getGuiObject( path, timeout, pollInterval )
//                .execute( this, script );
    }


    public Object configure( String script )
    {
        return camera.configure( script, this );
    }

    public void setProperties( Properties properties )
    {
        if ( logger.isDebugEnabled() )
        {
            logger.debug( "Received: " + properties );
        }

        camera.setProperties( properties );
    }


    public void notifyAWTEvents( long notificationEventMask )
    {
        if ( notificationEventMask < 1 )
        {
            eventNotification.uninstallAWTEventNotifications( notificationEventMask );
        }
        else
        {
            eventNotification.installAWTEventNotifications( notificationEventMask );
        }
    }

    public void notifyFXEvents( String eventType )
    {
        if ( eventType == null
             || eventType.isEmpty()
             || "NONE".equalsIgnoreCase( eventType ) )
        {
            eventNotification.uninstallFXEventNotifications( Event.ANY );
        }
        else
        {
            eventNotification.installFXEventNotifications( Event.ANY );
        }
    }

    public void notifyDOMEvents( String eventTypes )
    {
        if ( eventTypes == null || eventTypes.isEmpty() || "NONE".equalsIgnoreCase( eventTypes ) )
        {
            eventNotification.uninstallDOMEventNotifications( Backend.STAR );
        }
        else
        {
            eventNotification.installDOMEventNotifications( eventTypes );
        }
    }


    public void notifySnapshotEventDelay( long delay )
    {
        DocumentNotificationBuffer.setDelay( delay );

        if ( delay > 0 )
        {
            DocumentNotificationBuffer.addDocumentListener( snapshotListener );

            if ( logger.isDebugEnabled() )
            {
                logger.debug( format( "Installed snapshot listener: delay=[%s]", delay ) );
            }
        }
        else
        {
            DocumentNotificationBuffer.removeDocumentListener( snapshotListener );

            if ( logger.isDebugEnabled() )
            {
                logger.debug( format( "Removed snapshot listener: delay=[%s]", delay ) );
            }
        }
    }

    @Override
    public void hashCache( int level )
    {
        camera.getObjectManager()
                .getHashCache()
                .setEnabled( level > 0 );
    }

    @Override
    public void gc()
    {
        camera
                .getObjectManager()
                .getHashCache()
                .gc();
    }


    public void logNotifications( int show )
    {
        eventNotification.setLogNotifications( show > 0 );
    }


    private DocumentNotificationBuffer.DocumentListener snapshotListener = ( source, document ) -> {

        XmlUtils.removeTrimmedEmptyTextNodes( document );

        final Notification notification = new Notification(
                "snapshot",
                source,
                notificationSequenceNumber++,
                XmlUtils.serialize( document, true, true ) );


        if ( logger.isDebugEnabled() )
        {
            logger.debug( format( "Sending Notification: type=[%s], seq[%s], timestamp=[%s].",
                    notification.getType(),
                    notification.getSequenceNumber(),
                    DateUtils.timestamp( notification.getTimeStamp() )
            ) );
        }

        sendNotification( notification );
    };
}
