package com.brentcroft.gtd.camera;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * A synthetic top-level parent for all AWT and FX (and HTML) gui components.
 *
 * Created by Alaric on 14/07/2017.
 */
public class Snapshot
{
    public < T extends Object > List< T > getChildren()
    {
        List children = new ArrayList<>();


//        children.addAll(
//                List.of( java.awt.Window.getWindows() ) );
//
//        children.addAll(
//                javafx.stage.Window.getWindows()
//                        .stream()
//                        .map( s -> s.getScene().getRoot() )
//                        .collect( Collectors.toList() )
//        );


        children
                .addAll( Arrays
                        .asList( java.awt.Window.getWindows() ) );

        try {

            children
                    .addAll(
                            com.sun.javafx.stage.StageHelper.getStages()
                                    .stream()
                                    .map(stage -> stage.getScene().getRoot())
                                    .collect(Collectors.toList())
                    );
        }
        catch (Exception e)
        {
            children.addAll(
                javafx.stage.Window.getWindows()
                        .stream()
                        .map( s -> s.getScene().getRoot() )
                        .collect( Collectors.toList() )
            );
        }

        return children;
    }
}
