package com.brentcroft.gtd.adapter.model.w3c;

import com.brentcroft.gtd.adapter.model.GuiObject;
import com.brentcroft.gtd.adapter.model.GuiObjectConsultant;
import com.brentcroft.gtd.camera.GuiCameraObjectManager;
import com.brentcroft.util.xpath.gob.Gob;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;
import java.util.stream.Stream;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.html.HTMLElement;
import org.w3c.dom.html.HTMLIFrameElement;

/**
 * Created by Alaric on 15/07/2017.
 */
public class W3cHTMLIFrameElementGuiObject< T extends HTMLIFrameElement > extends W3cHTMLElementGuiObject< T >
{
    public W3cHTMLIFrameElementGuiObject( T go, Gob parent, GuiObjectConsultant< T > guiObjectConsultant, GuiCameraObjectManager objectManager )
    {
        super( go, parent, guiObjectConsultant, objectManager );
    }


    @Override
    public boolean hasChildren()
    {
        return null != getObject().getContentDocument();
    }

    @Override
    public List< GuiObject > loadChildren()
    {
        List< GuiObject > children = new ArrayList<>();

        Document content = getObject().getContentDocument();

        if ( content != null )
        {
            for ( Node child = content.getFirstChild(); child != null; child = child.getNextSibling() )
            {
                if ( child != null && child instanceof HTMLElement )
                {
                    children.add( getManager().adapt( child, this ) );
                }
            }
        }

        return children;
    }
}
