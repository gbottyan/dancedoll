/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package danceDoll;

import bvh.BVHAnimData;
import bvh.BVHAnimation;
import bvh.BVHBone;
import bvh.BVHChannel;
import com.jme3.animation.AnimChannel;
import com.jme3.animation.AnimControl;
import com.jme3.animation.AnimEventListener;
import com.jme3.animation.Animation;
import com.jme3.animation.Bone;
import com.jme3.animation.BoneTrack;
import com.jme3.animation.Skeleton;
import com.jme3.asset.AssetManager;
import com.jme3.math.FastMath;
import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;
import com.jme3.scene.Mesh;
import com.jme3.scene.VertexBuffer;
import com.jme3.scene.VertexBuffer.Format;
import com.jme3.scene.VertexBuffer.Type;
import com.jme3.scene.VertexBuffer.Usage;
import com.jme3.util.BufferUtils;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.Locale;
import java.util.Scanner;

/**
 *
 * @author master
 */
public class BVHController implements AnimEventListener {

    private String url = "";
    protected AssetManager assetManager;
    private Scanner scan;
    BVHAnimation animation;
    Animation anim;
    private String fileName;
    BVHAnimData data;

    /**
     * 
     * @param assetManager 
     * @param url URL to BVH-File
     * @throws IOException 
     */
    public BVHController(AssetManager assetManager, String url) throws IOException {
        this.assetManager = assetManager;
        URL bvhFile = DanceDoll.class.getClassLoader().getResource(url);
        fileName = bvhFile.getFile();
        FileInputStream fstream = new FileInputStream(bvhFile.getFile());

        InputStream in = bvhFile.openStream();
        try {
            scan = new Scanner(in);
            scan.useLocale(Locale.US);
            loadFromScanner();
        } finally {
            if (in != null) {
                in.close();
            }
        }

    }

    /**
     * Read all Parameter for one Bone
     * @param name Name of the Bone
     * @return Bone
     */
    private BVHBone readBone(String name) {
        BVHBone bone = new BVHBone(name);

        String token = scan.next();
        if (token.equals("{")) {
            token = scan.next();
            if (token.equals("OFFSET")) {
                bone.getOffset().setX(Float.parseFloat(scan.next()));
                bone.getOffset().setY(Float.parseFloat(scan.next()));
                bone.getOffset().setZ(Float.parseFloat(scan.next()));
                token = scan.next();
            }
            if (token.equals("CHANNELS")) {
                bone.setChannels(new ArrayList<BVHChannel>());
                int nbChan = Integer.parseInt(scan.next());
                for (int i = 0; i < nbChan; i++) {
                    bone.getChannels().add(new BVHChannel(scan.next()));
                }
                token = scan.next();
            }
            while (token.equals("JOINT") || token.equals("End")) {
                if (bone.getChildren() == null) {
                    bone.setChildren(new ArrayList<BVHBone>());
                }
                bone.getChildren().add(readBone(scan.next()));
                token = scan.next();
            }
        }

        return bone;
    }

    /**
     * Reads the BVH-File from the scanner and starts the funktion for the differen Parts in the BVH-File 
     * @throws IOException 
     */
    private void loadFromScanner() throws IOException {
        animation = new BVHAnimation();
        String token = scan.next();

        if (token.equals("HIERARCHY")) {
            token = scan.next();

            if (token.equals("ROOT")) {
                token = scan.next();

                animation.setHierarchy(readBone(token));
                token = scan.next();
            }
        }
        if (token.equals("MOTION")) {
            scan.next();
            animation.setNbFrames(scan.nextInt());
            scan.next();
            scan.next();
            animation.setFrameTime(scan.nextFloat());
            for (int i = 0; i < animation.getNbFrames(); i++) {
                readChanelsValue(animation.getHierarchy());
            }
        }
        compileData();
    }

    private void compileData() {
        Bone[] bones = new Bone[animation.getHierarchy().getNbBones()];
        index = 0;
        BoneTrack[] tracks = new BoneTrack[animation.getHierarchy().getNbBones()];
        populateBoneList(bones, tracks, animation.getHierarchy(), null);
        Skeleton skeleton = new Skeleton(bones);
        String animName = fileName.substring(fileName.lastIndexOf("/") + 1).replaceAll(".bvh", "");
        float animLength = animation.getFrameTime() * animation.getNbFrames();
        Animation boneAnimation = new Animation(animName, animLength);
        boneAnimation.setTracks(tracks);
        data = new BVHAnimData(skeleton, boneAnimation, animation.getFrameTime());
    }
    int index = 0;

    private void populateBoneList(Bone[] bones, BoneTrack[] tracks, BVHBone hierarchy, Bone parent) {

        Bone bone = new Bone(hierarchy.getName());

        bone.setBindTransforms(hierarchy.getOffset(), Quaternion.IDENTITY, Vector3f.ZERO);

        if (parent != null) {
            parent.addChild(bone);
        }
        bones[index] = bone;
        tracks[index] = getBoneTrack(hierarchy);
        index++;
        if (hierarchy.getChildren() != null) {
            for (BVHBone bVHBone : hierarchy.getChildren()) {
                populateBoneList(bones, tracks, bVHBone, bone);
            }
        }
    }

    private BoneTrack getBoneTrack(BVHBone bone) {
        float[] times = new float[animation.getNbFrames()];
        Vector3f[] translations = new Vector3f[animation.getNbFrames()];
        Quaternion[] rotations = new Quaternion[animation.getNbFrames()];
        float time = 0;

        Quaternion rx = new Quaternion();
        Quaternion ry = new Quaternion();
        Quaternion rz = new Quaternion();
        for (int i = 0; i < animation.getNbFrames(); i++) {
            times[i] = time;

            Vector3f t = new Vector3f(Vector3f.ZERO);
            Quaternion r = new Quaternion(Quaternion.IDENTITY);
            rx.set(Quaternion.IDENTITY);
            ry.set(Quaternion.IDENTITY);
            rz.set(Quaternion.IDENTITY);
            if (bone.getChannels() != null) {
                for (BVHChannel bVHChannel : bone.getChannels()) {


                    if (bVHChannel.getName().equals(BVHChannel.BVH_CHANNEL_X_POSITION)) {
                        t.setX(bVHChannel.getValues().get(i));
                    }
                    if (bVHChannel.getName().equals(BVHChannel.BVH_CHANNEL_Y_POSITION)) {
                        t.setY(bVHChannel.getValues().get(i));
                    }
                    if (bVHChannel.getName().equals(BVHChannel.BVH_CHANNEL_Z_POSITION)) {
                        t.setZ(bVHChannel.getValues().get(i));
                    }
                    if (bVHChannel.getName().equals(BVHChannel.BVH_CHANNEL_X_ROTATION)) {

                        rx.fromAngleAxis((bVHChannel.getValues().get(i)) * FastMath.DEG_TO_RAD, Vector3f.UNIT_X);
                    }
                    if (bVHChannel.getName().equals(BVHChannel.BVH_CHANNEL_Y_ROTATION)) {
                        ry.fromAngleAxis((bVHChannel.getValues().get(i)) * FastMath.DEG_TO_RAD, Vector3f.UNIT_Y);
                    }
                    if (bVHChannel.getName().equals(BVHChannel.BVH_CHANNEL_Z_ROTATION)) {
                        rz.fromAngleAxis((bVHChannel.getValues().get(i)) * FastMath.DEG_TO_RAD, Vector3f.UNIT_Z);
                    }
                }

                r.multLocal(rz).multLocal(rx).multLocal(ry);
            }
            translations[i] = t;
            rotations[i] = r;
            time += animation.getFrameTime();
        }

        return new BoneTrack(index, times, translations, rotations);
    }

    private void readChanelsValue(BVHBone bone) {
        if (bone.getChannels() != null) {
            for (BVHChannel bvhChannel : bone.getChannels()) {
                if (bvhChannel.getValues() == null) {
                    bvhChannel.setValues(new ArrayList<Float>());
                }
                bvhChannel.getValues().add(scan.nextFloat());
            }
            for (BVHBone b : bone.getChildren()) {
                readChanelsValue(b);
            }
        }
    }

    public void createBindPose(Mesh mesh) {
        VertexBuffer pos = mesh.getBuffer(Type.Position);
        if (pos == null || mesh.getBuffer(Type.BoneIndex) == null) {
            return;
        }

        VertexBuffer bindPos = new VertexBuffer(Type.BindPosePosition);
        bindPos.setupData(Usage.CpuOnly,
                3,
                Format.Float,
                BufferUtils.clone(pos.getData()));
        mesh.setBuffer(bindPos);

        pos.setUsage(Usage.Stream);

        VertexBuffer norm = mesh.getBuffer(Type.Normal);
        if (norm != null) {
            VertexBuffer bindNorm = new VertexBuffer(Type.BindPoseNormal);
            bindNorm.setupData(Usage.CpuOnly,
                    3,
                    Format.Float,
                    BufferUtils.clone(norm.getData()));
            mesh.setBuffer(bindNorm);
            norm.setUsage(Usage.Stream);
        }
    }

    public void onAnimCycleDone(AnimControl control, AnimChannel channel, String animName) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public void onAnimChange(AnimControl control, AnimChannel channel, String animName) {
        throw new UnsupportedOperationException("Not supported yet.");
    }
}
