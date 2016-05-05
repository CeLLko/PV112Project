package cz.muni.fi.pv112.project.util;

import org.joml.Vector3f;

import java.io.*;
import java.util.*;

public class SceneObject implements Serializable{

    private Geometry geometry;
    private List<Material> materials = new ArrayList<>();
    private Material normalMap = null;
    private Vector3f position;
    private float rotation;
    private float scale;

    public SceneObject(Geometry geometry) {
        this.geometry = geometry;
    }

    public Geometry getGeometry() {
        return geometry;
    }

    public void setGeometry(Geometry geometry) {
        this.geometry = geometry;
    }

    public List<Material> getMaterials() {
        return materials;
    }

    public void setMaterials(List<Material> materials) {this.materials = materials;}

    public void addMaterial(Material material){ this.materials.add(material);}

    public Material getNormalMap() {
        return normalMap;
    }

    public void setNormalMap(Material material) {this.normalMap = material;}

    public Vector3f getPosition(){ return this.position;}

    public void setPosition(Vector3f position){ this.position=position;}

    public float getRotation() {return rotation;}

    public void setRotation(float rotation){ this.rotation=rotation;}

    public float getScale() {return scale;}

    public void setScale(float scale){ this.scale=scale;}

}
