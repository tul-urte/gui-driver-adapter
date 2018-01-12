package com.brentcroft.gtd.adapter.utils;

import java.util.Map;

/**
 * Created by Alaric on 11/05/2017.
 */
public enum DataLimit
{
    MAX_TEXT_LENGTH( 100 ),
    MAX_URL_LENGTH( 100 ),
    MAX_TREE_DEPTH( 3 ),
    MAX_COMBO_DEPTH( 10 ),
    MAX_LIST_DEPTH( 10 ),
    MAX_TABLE_ROWS( 3 ),
    MAX_TABLE_COLUMNS( 3 );

    private int min;

    DataLimit( int min )
    {
        this.min = min;
    }

    public int getMin()
    {
        return min;
    }

    public int getMin( int currentMax, Map< String, Object > options )
    {
        if ( options != null && options.containsKey( name() ) )
        {
            Object option = options.get( name() );

            if ( option != null )
            {
                try
                {
                    return Math.min( currentMax, Integer.parseInt( option.toString() ) );
                }
                catch ( Exception e )
                {
                    e.printStackTrace();
                }
            }
        }
        return Math.min( currentMax, min );
    }

    public String maybeTruncate( String text )
    {
        return maybeTruncate( text, null );
    }

    public String maybeTruncate( String text, Map< String, Object > options )
    {
        if ( text == null )
        {
            return null;
        }

        return text
                .substring(
                        0,
                        getMin(
                                text.length(),
                                options ) );
    }
}