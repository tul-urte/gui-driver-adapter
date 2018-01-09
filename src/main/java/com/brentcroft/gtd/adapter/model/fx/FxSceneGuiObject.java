package com.brentcroft.gtd.adapter.model.fx;

import com.brentcroft.gtd.adapter.model.AbstractGuiObject;
import com.brentcroft.gtd.adapter.model.GuiObject;
import com.brentcroft.gtd.adapter.model.GuiObjectConsultant;
import com.brentcroft.gtd.camera.GuiCameraObjectManager;
import com.brentcroft.util.xpath.gob.Gob;
import java.util.ArrayList;
import java.util.List;
import javafx.scene.Scene;

/**
 * Created by Alaric on 15/07/2017.
 */
public class FxSceneGuiObject< T extends Scene > extends AbstractGuiObject< T >
{
    public FxSceneGuiObject( T go, Gob parent, GuiObjectConsultant< T > guiObjectConsultant, GuiCameraObjectManager objectManager )
    {
        super( go, parent, guiObjectConsultant, objectManager );
    }


    @Override
    public List< GuiObject > loadChildren()
    {
        List< GuiObject > children = new ArrayList<>();

        children.add( getManager().adapt( getObject().getRoot(), this ) );

        return children;
    }
}
