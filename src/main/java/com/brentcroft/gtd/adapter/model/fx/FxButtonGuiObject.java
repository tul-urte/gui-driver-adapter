package com.brentcroft.gtd.adapter.model.fx;

import com.brentcroft.gtd.adapter.model.GuiObject;
import com.brentcroft.gtd.adapter.model.GuiObjectConsultant;
import com.brentcroft.gtd.adapter.utils.FXUtils;
import com.brentcroft.gtd.camera.GuiCameraObjectManager;
import com.brentcroft.util.xpath.gob.Gob;
import java.util.Map;
import javafx.scene.control.Button;
import org.w3c.dom.Element;

/**
 * Created by Alaric on 15/07/2017.
 */
public class FxButtonGuiObject< T extends Button > extends FxLabeledGuiObject< T > implements GuiObject.Click
{
    public FxButtonGuiObject( T go, Gob parent, GuiObjectConsultant< T > guiObjectConsultant, GuiCameraObjectManager objectManager )
    {
        super( go, parent, guiObjectConsultant, objectManager );
    }

    @Override
    public void buildProperties( Element element, Map< String, Object > options )
    {
        super.buildProperties( element, options );

        addClickAction( element, options );
    }

    @Override
    public void click()
    {
        FXUtils.maybeInvokeNowOnFXThread( getObject()::fire );
    }
}
