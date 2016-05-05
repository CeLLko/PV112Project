package cz.muni.fi.pv112.project.util;

/** red=terrain shape, blue = cows,  green = trees
 * Created by Adam Gdovin on 4/29/2016.
 */
public class Terrain {
    private int subdivisions;
    private float tileSize;


    private Shape terrainShape;

    public Terrain(int subdivisions, float tileSize){
        this.subdivisions = subdivisions;
        this.tileSize = tileSize;
    }

    public void setTerrainShape(Shape terrainShape){ this.terrainShape = terrainShape; }
    public Shape getTerrainShape(){ return this.terrainShape; }

    public void setSubdivisions(int subdivisions){ this.subdivisions = subdivisions; }
    public int getSubdivisions(){ return this.subdivisions; }

    public void setTileSize(float tileSize) {this.tileSize = tileSize;}
    public float getTileSite(){ return this.tileSize; }

}
