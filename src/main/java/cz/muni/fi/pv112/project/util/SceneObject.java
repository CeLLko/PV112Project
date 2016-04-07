package cz.muni.fi.pv112.project.util;

public class SceneObject {

    private Geometry geometry;
    private Material material;

    public SceneObject(Geometry geometry, Material material) {
        this.geometry = geometry;
        this.material = material;
    }

    public Geometry getGeometry() {
        return geometry;
    }

    public void setGeometry(Geometry geometry) {
        this.geometry = geometry;
    }

    public Material getMaterial() {
        return material;
    }

    public void setMaterial(Material material) {
        this.material = material;
    }
}
