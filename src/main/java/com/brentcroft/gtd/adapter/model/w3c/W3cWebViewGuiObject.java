package com.brentcroft.gtd.adapter.model.w3c;

import com.brentcroft.gtd.adapter.model.GuiObject;
import com.brentcroft.gtd.adapter.model.GuiObjectConsultant;
import com.brentcroft.gtd.adapter.model.fx.FxParentGuiObject;
import com.brentcroft.gtd.camera.GuiCameraObjectManager;
import com.brentcroft.util.xpath.gob.Gob;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import javafx.scene.web.WebView;

/**
 * Created by Alaric on 15/07/2017.
 */
public class W3cWebViewGuiObject< T extends WebView > extends FxParentGuiObject< T >
{
    public W3cWebViewGuiObject( T go, Gob parent, GuiObjectConsultant< T > guiObjectConsultant, GuiCameraObjectManager hgom )
    {
        super( go, parent, guiObjectConsultant, hgom );
    }

    @Override
    public boolean hasChildren()
    {
        return true;
    }

    @Override
    public List< GuiObject > loadChildren()
    {
        List< GuiObject > children = new ArrayList<>();

        Optional
                .ofNullable( getObject().getEngine() )
                .ifPresent( engine ->
                {
                    Optional
                            .ofNullable( engine.getDocument() )
                            .ifPresent( document ->
                            {
                                Optional
                                        .ofNullable( document.getDocumentElement() )
                                        .ifPresent( de -> children.add( getManager().adapt( de, this ) ) );
                            } );
                } );

        return children;
    }
}
