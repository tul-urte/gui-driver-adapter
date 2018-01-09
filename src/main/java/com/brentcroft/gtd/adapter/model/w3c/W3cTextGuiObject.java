package com.brentcroft.gtd.adapter.model.w3c;

import com.brentcroft.gtd.adapter.model.DefaultGuiObject;
import com.brentcroft.gtd.adapter.model.GuiObject;
import com.brentcroft.gtd.adapter.model.GuiObjectConsultant;
import com.brentcroft.gtd.adapter.utils.FXUtils;
import com.brentcroft.gtd.camera.GuiCameraObjectManager;
import com.brentcroft.util.xpath.gob.Gob;
import java.util.Map;
import java.util.Optional;
import org.w3c.dom.Element;
import org.w3c.dom.Text;

/**
 * Created by Alaric on 15/07/2017.
 */
public class W3cTextGuiObject< T extends Text > extends DefaultGuiObject< T >// implements GuiObject.Text
{
    public W3cTextGuiObject( T go, Gob parent, GuiObjectConsultant< T > guiObjectConsultant, GuiCameraObjectManager objectManager )
    {
        super( go, parent, guiObjectConsultant, objectManager );
    }

    /**
     * Returns \"text\".<br/>
     *
     * @return \"text\"
     */
    @Override
    public String getComponentTag()
    {
        return "text";
    }


    @Override
    public void buildProperties( Element element, Map< String, Object > options )
    {
        super.buildProperties( element, options );

        Optional
                .ofNullable( element.getOwnerDocument() )
                .ifPresent( document -> element.appendChild( document.createTextNode( getObject().getData().trim() ) ) );

        // addTextAction( element, options );
    }

//
//    @Override
//    public String getText()
//    {
//        return getObject().getData();
//    }
//
//    @Override
//    public void setText( String text )
//    {
//        FXUtils.maybeInvokeNowOnFXThread( () -> getObject().setData( text ) );
//    }
}
