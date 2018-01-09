package com.brentcroft.gtd.adapter.model.fx;

import com.brentcroft.gtd.adapter.model.GuiObject;
import com.brentcroft.gtd.adapter.model.GuiObjectConsultant;
import com.brentcroft.gtd.camera.GuiCameraObjectManager;
import com.brentcroft.util.xpath.gob.Gob;
import java.util.ArrayList;
import java.util.List;
import javafx.scene.control.Accordion;

/**
 * Created by Alaric on 15/07/2017.
 */
public class FxAccordionGuiObject< T extends Accordion > extends FxControlGuiObject< T >
{
    public FxAccordionGuiObject( T go, Gob parent, GuiObjectConsultant< T > guiObjectConsultant, GuiCameraObjectManager objectManager )
    {
        super( go, parent, guiObjectConsultant, objectManager );
    }

    @Override
    public boolean hasChildren()
    {
        return ! getObject().getPanes().isEmpty();
    }

    @Override
    public List< GuiObject > loadChildren()
    {
        List< GuiObject > children = new ArrayList<>();

        getObject()
                .getPanes()
                .forEach( child -> children.add( getManager().adapt( child, this ) ) );

        children.addAll( super.loadChildren() );

        return children;
    }
}
