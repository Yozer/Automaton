package agh.edu.pl.gui.structures;

/**
 * Created by Dominik on 2015-12-20.
 */
public class StructureInfo
{
    private String name;
    private int width;
    private int height;
    private String path;

    public StructureInfo(String name, int width, int height, String path)
    {
        this.name = name;
        this.width = width;
        this.height = height;
        this.path = path;
    }

    public String getName()
    {
        return name;
    }
    public int getWidth()
    {
        return width;
    }

    public int getHeight()
    {
        return height;
    }

    String getPath()
    {
        return path;
    }

    @Override
    public String toString()
    {
        return getName();
    }
}
