package DataModel;

/**
 * Created by nisan on 21/05/2016.
 */
public class Cell {
    /* attributes */
    int x;
    int y;
    int z;

    public Cell()
    {
        this.x =0;
        this.y=0;
        this.z = 0;
    }

    public Cell(int x,int y,int z)
    {
        this.x =x;
        this.y =y;
        this.z = z;
    }

    public int GetX()
    {
        return this.x;
    }

    public int GetY()
    {
        return this.y;
    }

    public int GetZ()
    {
        return this.z;
    }

    public void Print()
    {
        System.out.println(this.x +  " " + this.y + " " + this.z);
    }


}
