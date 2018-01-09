package com.brentcroft.gtd.adapter.model.fx;

import com.brentcroft.gtd.adapter.model.AttrSpec;
import com.brentcroft.gtd.adapter.model.GuiObject;
import com.brentcroft.gtd.adapter.model.GuiObjectConsultant;
import com.brentcroft.gtd.adapter.utils.FXUtils;
import com.brentcroft.gtd.camera.GuiCameraObjectManager;
import com.brentcroft.util.xpath.gob.Gob;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import javafx.scene.web.HTMLEditor;
import org.w3c.dom.Element;

/**
 * Created by Alaric on 15/07/2017.
 */
public class FxHTMLEditorGuiObject< T extends HTMLEditor > extends FxControlGuiObject< T > implements GuiObject.Text
{

    public FxHTMLEditorGuiObject( T go, Gob parent, GuiObjectConsultant< T > guiObjectConsultant, GuiCameraObjectManager objectManager )
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
        return getObject().getHtmlText();
    }

    @Override
    public void setText( String text )
    {
        FXUtils.maybeInvokeNowOnFXThread( () -> getObject().setHtmlText( text ) );
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

    // "text", "size", "selected-index"
    enum Attr implements AttrSpec< HTMLEditor >
    {
        TEXT( "text", go -> "" + go.getHtmlText() );

        final String n;
        final Function< HTMLEditor, String > f;

        Attr( String name, Function< HTMLEditor, String > f )
        {
            this.n = name;
            this.f = f;
        }

        public String getName()
        {
            return n;
        }

        @Override
        public String getAttribute( HTMLEditor go )
        {
            return f.apply( go );
        }
    }
}
