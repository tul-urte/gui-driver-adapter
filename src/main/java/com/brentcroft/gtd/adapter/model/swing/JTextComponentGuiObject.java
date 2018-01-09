package com.brentcroft.gtd.adapter.model.swing;

import com.brentcroft.gtd.adapter.model.AttrSpec;
import com.brentcroft.gtd.adapter.model.GuiObject;
import com.brentcroft.gtd.adapter.model.GuiObjectConsultant;
import com.brentcroft.gtd.adapter.utils.SwingUtils;
import com.brentcroft.gtd.camera.GuiCameraObjectManager;
import com.brentcroft.util.xpath.gob.Gob;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import javax.swing.text.JTextComponent;
import org.w3c.dom.Element;

/**
 * Created by Alaric on 14/07/2017.
 */
public class JTextComponentGuiObject< T extends JTextComponent > extends JComponentGuiObject< T > implements GuiObject.Text, GuiObject.Click
{
    public JTextComponentGuiObject( T go, Gob parent, GuiObjectConsultant< T > guiObjectConsultant, GuiCameraObjectManager objectManager )
    {
        super( go, parent, guiObjectConsultant, objectManager );
    }


    @Override
    public void buildProperties( Element element, Map< String, Object > options )
    {
        super.buildProperties( element, options );

        addTextAction( element, options );
    }


    @Override
    public String getText()
    {
        return getObject().getText();
    }

    @Override
    public void setText( String text )
    {
        SwingUtils.maybeInvokeNowOnEventThread( () -> getObject().setText( text ) );
    }

    @Override
    public void click()
    {
        getObject().requestFocus();
        //RobotUtils.awtRobotClick( getObjectMidpointLocationOnScreen() );
    }


    @Override
    public List< AttrSpec > loadAttrSpec()
    {
        if ( attrSpec == null )
        {
            attrSpec = super.loadAttrSpec();
            attrSpec.addAll( Arrays.asList( Attr.values() ) );
        }

        return attrSpec;
    }

    enum Attr implements AttrSpec< JTextComponent >
    {
        TEXT( "text", go -> "" + go.getText() );

        final String n;
        final Function< JTextComponent, String > f;

        Attr( String name, Function< JTextComponent, String > f )
        {
            this.n = name;
            this.f = f;
        }

        public String getName()
        {
            return n;
        }

        @Override
        public String getAttribute( JTextComponent go )
        {
            return f.apply( go );
        }
    }
}
