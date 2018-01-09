package com.brentcroft.gtd.camera;

import com.brentcroft.gtd.adapter.model.GuiObject;
import com.brentcroft.gtd.adapter.model.GuiObjectVisitor;
import com.brentcroft.gtd.driver.GuiControllerMBean;
import com.brentcroft.gtd.driver.GuiObjectLocator;
import com.brentcroft.gtd.driver.GuiObjectService;
import com.brentcroft.util.XmlUtils;
import java.io.StringReader;
import java.util.Map;
import java.util.Properties;
import javax.script.Bindings;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

import static java.lang.String.format;

/**
 * Created by Alaric on 14/07/2017.
 */
public class Camera implements GuiObjectLocator< GuiObject >, GuiObjectService
{
    private GuiCameraObjectService service = new GuiCameraObjectService();

    public Camera()
    {
        service.install( new Properties() );
    }

    @Override
    public void shutdown()
    {
    }

    @Override
    public Object configure( String script, GuiControllerMBean driver )
    {
        try
        {
            ScriptEngine engine = new ScriptEngineManager().getEngineByName( "js" );

            Bindings b = engine.createBindings();

            b.put( "camera", this );
            b.put( "driver", driver );
            b.put( "service", service );

            return "" + engine.eval( new StringReader( script ), b );
        }
        catch ( ScriptException e )
        {
            throw new RuntimeException( format( "ScriptException file=[%s], line=[%s,%s]: %s",
                    e.getFileName(),
                    e.getLineNumber(),
                    e.getColumnNumber(),
                    e.getMessage() ) );
        }
    }

    public void setProperties( Properties properties )
    {
        service.install( properties );
    }

    @Override
    public GuiObjectLocator< GuiObject > getGuiObjectLocator()
    {
        return this;
    }


    public GuiCameraObjectManager getObjectManager()
    {
        return service.getManager();
    }

    @Override
    public String getReport()
    {
        return service.getManager().toString();
    }

    @Override
    public GuiCameraControllerMBean getController()
    {
        return new GuiCameraController( this );
    }


    public Object getOrigin()
    {
        return new Snapshot();
    }

    public GuiObject getGob( Object gobee )
    {
        return service
                .getManager()
                .adapt(
                        gobee == null ?
                                getOrigin() :
                                gobee,
                        null );
    }


    public void snapshot( Node node, Map< String, Object > options )
    {
        snapshot( null, node, options );
    }

    public void snapshot( Object origin, Node node, Map< String, Object > options )
    {
        getGob( origin ).accept( new GuiObjectVisitor( node, options ) );
    }


    public Document takeSnapshot()
    {
        return takeSnapshot( null );
    }

    public synchronized Document takeSnapshot( Map< String, Object > options )
    {
        return takeSnapshot( null, options );
    }

    public void takeSnapshot( Object origin, Node node, Map< String, Object > options )
    {
        snapshot( origin, node, options );
    }


    public synchronized Document takeSnapshot( Object origin, Map< String, Object > options )
    {
        Document document = XmlUtils.newDocument();


        long started = System.currentTimeMillis();

        snapshot( origin, document, options );


        long finished = System.currentTimeMillis();

        long duration = ( finished - started );

        Element element = document.getDocumentElement();

        if ( element != null )
        {
            element.setAttribute( "duration", "" + duration );
        }

        return document;
    }
}