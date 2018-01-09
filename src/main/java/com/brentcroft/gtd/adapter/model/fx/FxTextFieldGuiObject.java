package com.brentcroft.gtd.adapter.model.fx;

import com.brentcroft.gtd.adapter.model.GuiObjectConsultant;
import com.brentcroft.gtd.camera.GuiCameraObjectManager;
import com.brentcroft.util.xpath.gob.Gob;
import javafx.scene.control.TextField;

/**
 * Created by Alaric on 15/07/2017.
 */
public class FxTextFieldGuiObject< T extends TextField > extends FxTextInputControlGuiObject< T >
{
    public FxTextFieldGuiObject( T go, Gob parent, GuiObjectConsultant< T > guiObjectConsultant, GuiCameraObjectManager objectManager )
    {
        super( go, parent, guiObjectConsultant, objectManager );
    }

}
