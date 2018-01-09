package com.brentcroft.gtd.adapter.model;

import com.brentcroft.gtd.camera.GuiCameraObjectManager;
import com.brentcroft.gtd.driver.ObjectLostException;
import com.brentcroft.util.xpath.gob.Attribute;
import com.brentcroft.util.xpath.gob.Gob;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Function;

import static com.brentcroft.util.XmlUtils.getClassIdentifier;
import static java.lang.String.format;

/**
 * Created by Alaric on 14/07/2017.
 */
public abstract class AbstractGuiObject< T > implements GuiObject< T >
{
    // values
    protected String ATTRIBUTE_VALUE_TAB = "tab";
    protected String ATTRIBUTE_VALUE_TABS = "tabs";
    protected String ATTRIBUTE_VALUE_TABLE = "table";
    protected String ATTRIBUTE_VALUE_TREE = "tree";
    protected String ATTRIBUTE_VALUE_INDEX = "index";
    protected String ATTRIBUTE_VALUE_TEXT = "text";
    protected String ATTRIBUTE_VALUE_ROBOT = "robot";
    protected String ATTRIBUTE_VALUE_CLICK = "click";

    protected String ATTRIBUTE_HREF = "href";


    protected final Gob parent;
    private List< Attribute > attributes;
    private Set< ? extends GuiObject > children;

    protected final int guiObjectKey;
    protected final transient WeakReference< T > guiObject;
    protected final GuiCameraObjectManager manager;
    protected final GuiObjectConsultant< T > guiObjectConsultant;

    protected List< AttrSpec > attrSpec;


    public AbstractGuiObject( T go, Gob parent, GuiObjectConsultant< T > guiObjectConsultant, GuiCameraObjectManager objectManager )
    {
        this.guiObject = new WeakReference< T >( go );
        this.guiObjectKey = go.hashCode();
        this.parent = parent;
        this.guiObjectConsultant = guiObjectConsultant;
        this.manager = objectManager;
    }


    @Override
    public GuiObjectConsultant< T > getConsultant()
    {
        return guiObjectConsultant;
    }


    /**
     * The underlying foreign object, if it still exists.<p/>
     * If it doesn't exist any more then throws ObjectLostException.
     * <p/>
     * Be very careful NOT to allow implicit calls to the
     * underlying object's toString() method as this may
     * require being on the FX or Swing thread.
     *
     * @return The underlying foreign object, if it still exists
     */
    @Override
    public T getObject()
    {
        T object = guiObject.get();

        if ( object == null )
        {
            throw new ObjectLostException( format( "The object [%s] does not exist anymore.", guiObjectKey ) );
        }

        return object;
    }

    @Override
    public GuiObject< T > getThis()
    {
        return this;
    }

    public Gob getParent()
    {
        return parent;
    }


    @Override
    public GuiCameraObjectManager getManager()
    {
        return manager;
    }

    @Override
    public String getComponentTag()
    {
        return getObject() == null
                ? "null"
                : getClassIdentifier( getObject().getClass() );
    }


    /**
     * Never overridden.
     *
     * @return the cached attribute specifications
     */
    public final List< AttrSpec > getAttrSpec()
    {
        if ( attrSpec == null )
        {
            attrSpec = loadAttrSpec();
        }

        return attrSpec;
    }

    /**
     * Must be overridden by subtypes that extend attributes.
     *
     * @return the loaded attribute specifications
     */
    public List< AttrSpec > loadAttrSpec()
    {
        if ( attrSpec == null )
        {
            attrSpec = new ArrayList<>( Arrays.asList( Attr.values() ) );
        }

        return attrSpec;
    }


    enum Attr implements AttrSpec< Object >
    {
        HASH( "hash", go -> "" + go.hashCode() );

        final String n;
        final Function< Object, String > f;

        Attr( String name, Function< Object, String > f )
        {
            this.n = name;
            this.f = f;
        }

        public String getName()
        {
            return n;
        }

        @Override
        public String getAttribute( Object go )
        {
            return f.apply( go );
        }
    }


    @Override
    public final List< Attribute > getAttributes()
    {
        if ( attributes == null )
        {
            attributes = readAttributes();
        }

        return attributes;
    }


    @Override
    public final List< ? extends GuiObject > getChildren()
    {
        if ( children == null )
        {
            children = new HashSet<>( loadChildren() );
        }

        return children == null ? null : new ArrayList<>( children );
    }
}
