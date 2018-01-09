package com.brentcroft.gtd.adapter.model.fx;

import com.brentcroft.gtd.adapter.model.GuiObject;
import com.brentcroft.gtd.adapter.model.GuiObjectConsultant;
import com.brentcroft.gtd.camera.GuiCameraObjectManager;
import com.brentcroft.util.xpath.gob.Gob;
import java.util.List;
import javafx.scene.control.TextArea;

/**
 * Created by Alaric on 15/07/2017.
 */
public class FxTextAreaGuiObject< T extends TextArea > extends FxTextInputControlGuiObject< T >
{
    public FxTextAreaGuiObject( T go, Gob parent, GuiObjectConsultant< T > guiObjectConsultant, GuiCameraObjectManager objectManager )
    {
        super( go, parent, guiObjectConsultant, objectManager );
    }

    @Override
    public boolean hasChildren()
    {
        return false;
    }

    @Override
    public List< GuiObject > loadChildren()
    {
        return null;
    }

}
