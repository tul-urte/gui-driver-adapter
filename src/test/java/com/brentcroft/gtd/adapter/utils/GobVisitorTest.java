package com.brentcroft.gtd.adapter.utils;

import com.brentcroft.gtd.adapter.model.GuiObject;
import com.brentcroft.gtd.camera.Camera;
import com.brentcroft.gtd.camera.GuiCameraObjectService;
import com.brentcroft.gtd.camera.Snapshot;
import com.brentcroft.util.XmlUtils;
import java.util.Properties;
import org.junit.Test;

public class GobVisitorTest
{
    GuiCameraObjectService gos = new GuiCameraObjectService();


    @Test
    public void visit()
    {
        gos.install( new Properties() );

        GuiObject origin = gos
                .getManager()
                .adapt( new Snapshot(), null );

//        XPathGuiObjectVisitor visitor = new XPathGuiObjectVisitor();
//
//        XPathUtils.visitPath(
//                visitor,
//                origin,
//                "Snapshot[ @timestamp ]/bollocks" );
//
//        System.out.println( visitor.getSelected() );


        Camera c = new Camera();

        System.out.println( c.getReport() );

        System.out.println( XmlUtils.serialize( c.takeSnapshot() ) );
    }
}