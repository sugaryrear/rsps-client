package com.ferox.entity.model;

import com.ferox.cache.anim.Animation;
import com.ferox.cache.anim.Skins;
import com.ferox.draw.Rasterizer2D;
import com.ferox.draw.Rasterizer3D;
import com.ferox.entity.Renderable;
import com.ferox.io.Buffer;
import com.ferox.model.texture.TextureCoordinate;
import com.ferox.net.requester.Provider;
import com.ferox.scene.SceneGraph;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class Model extends Renderable {

    public static int anInt1620;
    public static Model EMPTY_MODEL = new Model(true);
    public static boolean obj_exists; //obj_exists
    public static int anInt1685;
    public static int anInt1686;
    public static int anInt1687;
    public static long anIntArray1688[] = new long[1000];
    public static int SINE[];
    public static int COSINE[];
    static ModelHeader aClass21Array1661[];
    static Provider resourceProvider;
    static boolean hasAnEdgeToRestrict[] = new boolean[4700];
    static boolean outOfReach[] = new boolean[4700];
    static int projected_vertex_x[] = new int[4700];
    static int projected_vertex_y[] = new int[4700];
    static int projected_vertex_z[] = new int[4700];
    static int camera_vertex_x[] = new int[4700];
    static int camera_vertex_y[] = new int[4700];
    static int camera_vertex_z[] = new int[4700];
    static int depthListIndices[] = new int[1600];
    static int faceLists[][] = new int[1600][512];
    static int anIntArray1673[] = new int[12];
    static int anIntArrayArray1674[][] = new int[12][2000];
    static int anIntArray1675[] = new int[2000];
    static int anIntArray1676[] = new int[2000];
    static int anIntArray1677[] = new int[12];
    static int anIntArray1678[] = new int[10];
    static int anIntArray1679[] = new int[10];
    static int anIntArray1680[] = new int[10];
    static int xAnimOffset;
    static int yAnimOffset;
    static int zAnimOffset;
    static int modelIntArray3[];
    static int modelIntArray4[];
    private static int anIntArray1622[] = new int[2000];
    private static int anIntArray1623[] = new int[2000];
    private static int anIntArray1624[] = new int[2000];
    private static int anIntArray1625[] = new int[2000];

    static {
        SINE = Rasterizer3D.SINE;
        COSINE = Rasterizer3D.COSINE;
        modelIntArray3 = Rasterizer3D.HSL_TO_RGB;
        modelIntArray4 = Rasterizer3D.anIntArray1469;
    }

    public int[] face_material;
    public byte[] face_texture;
    public byte[] texture_map;
    public int vertices;
    public int vertex_x[];
    public int vertex_y[];
    public int vertex_z[];
    public int faces;
    public int triangle_edge_a[];
    public int triangle_edge_b[];
    public int triangle_edge_c[];
    public int faceHslA[];
    public int faceHslB[];
    public int faceHslC[];
    public int render_type[];
    public byte face_render_priorities[];
    public int face_alpha[];
    public int[] face_color;
    public byte face_priority = 0;
    public int texture_faces;
    public int[] triangle_texture_edge_a;
    public int[] triangle_texture_edge_b;
    public int[] triangle_texture_edge_c;
    public int min_x;
    public int max_x;
    public int max_z;
    public int min_z;
    public int diagonal_2D;
    public int max_y;
    public int scene_depth;
    public int diagonal_3D;
    public int obj_height;
    public int bone_skin[];
    public int muscle_skin[];
    public int vertex_skin[][];
    public int face_skin[][];
    public boolean within_tile;
    public Vertex gouraud_vertex[];
    private boolean aBoolean1618;

    private static final Set<Integer> repeatedTextureModels = new HashSet<>();

    static {
        int[] array = {55555, 55556, 55557, 55558, 55559, 55560, 55561, 55562, 55563, 55564, 55565, 55566, 55567, 55568, 55569, 55570, 55571, 55572, 55573, 55574, 55575, 55576, 55577, 55578, 55579, 55580, 55581, 55582, 55583, 55584, 55585, 55586, 55587, 55588, 55589, 55590, 55591, 55592, 55593,11204, 55594, 55595, 55596, 55597, 55598, 55599, 55600, 55601, 55602, 55603, 55604, 55605, 55606};
        for (int id : array) {
            addAll(id);
        }
    }

    private static void addAll(int... values) {
        for (int value : values) {
            repeatedTextureModels.add(value);
        }
    }

int[][] field2180;//todo new osrs anim stuff
    int[][] field2181;//todo new osrs anim stuff

    public void decodeOSRS1(byte data[]) {
        Buffer header = new Buffer(data);
        Buffer nc2 = new Buffer(data);
        Buffer nc3 = new Buffer(data);
        Buffer nc4 = new Buffer(data);
        Buffer nc5 = new Buffer(data);
        Buffer nc6 = new Buffer(data);
        Buffer nc7 = new Buffer(data);
        header.pos = data.length - 26;
        vertices  = header.readUShort();
        faces  = header.readUShort();
        texture_faces  = header.readUByte();
        int flags = header.readUByte();
        int priority_opcode = header.readUByte();
        int alpha_opcode = header.readUByte();
        int tSkin_opcode = header.readUByte();
        int texture_opcode = header.readUByte();
        int vSkin_opcode = header.readUByte();
        int newVar = header.readUByte();
        int j3 = header.readUShort();
        int k3 = header.readUShort();
        int l3 = header.readUShort();
        int i4 = header.readUShort();
        int j4 = header.readUShort();
        int texture_id = 0;
        int texture_ = 0;
        int texture__ = 0;
        int face;
        face_color = new int[faces ];
        if (texture_faces  > 0) {
            texture_map = new byte[texture_faces ];
            header.pos = 0;
            for (face = 0; face < texture_faces ; face++) {
                byte opcode = texture_map[face] = header.readSignedByte();
                if (opcode == 0) {
                    texture_id++;
                }

                if (opcode >= 1 && opcode <= 3) {
                    texture_++;
                }
                if (opcode == 2) {
                    texture__++;
                }
            }
        }
        int pos;
        pos = texture_faces ;
        int vertexMod_offset = pos;
        pos += vertices ;

        int drawTypeBasePos = pos;
        if (flags == 1)
            pos += faces ;

        int faceMeshLink_offset = pos;
        pos += faces ;

        int facePriorityBasePos = pos;
        if (priority_opcode == 255)
            pos += faces ;

        int tSkinBasePos = pos;
        if (tSkin_opcode == 1)
            pos += faces ;

        int vSkinBasePos = pos;
        if (vSkin_opcode == 1)
            pos += vertices ;

        int alphaBasePos = pos;
        if (alpha_opcode == 1)
            pos += faces ;

        int faceVPoint_offset = pos;
        pos += i4;

        int textureIdBasePos = pos;
        if (texture_opcode == 1)
            pos += faces  * 2;

        int textureBasePos = pos;
        pos += j4;

        int color_offset = pos;
        pos += faces  * 2;

        int vertexX_offset = pos;
        pos += j3;

        int vertexY_offset = pos;
        pos += k3;

        int vertexZ_offset = pos;
        pos += l3;

        int mainBuffer_offset = pos;
        pos += texture_id * 6;

        int firstBuffer_offset = pos;
        pos += texture_ * 6;

        int secondBuffer_offset = pos;
        pos += texture_ * 6;

        int thirdBuffer_offset = pos;
        pos += texture_ * 2;

        int fourthBuffer_offset = pos;
        pos += texture_;

        int fifthBuffer_offset = pos;
        pos += texture_ * 2 + texture__ * 2;
        vertex_x  = new int[vertices ];
        vertex_y = new int[vertices ];
        vertex_z = new int[vertices ];
        triangle_edge_a  = new int[faces ];
        triangle_edge_b = new int[faces ];
        triangle_edge_c = new int[faces ];
        if (vSkin_opcode == 1)
            bone_skin = new int[vertices ];
        if (flags == 1)
            render_type = new int[faces ];
        if (priority_opcode == 255)
            face_render_priorities = new byte[faces ];
        else
            face_priority = (byte) priority_opcode;
        if (alpha_opcode == 1)
            face_alpha = new int[faces ];
        if (tSkin_opcode == 1)
            muscle_skin = new int[faces ];
        if (texture_opcode == 1)
            face_material = new int[faces ];
        if (texture_opcode == 1 && texture_faces  > 0)
            face_texture = new byte[faces];


        if (newVar == 1) { // L: 169
            this.field2180 = new int[vertices ][]; // L: 170
            this.field2181 = new int[vertices ][]; // L: 171
        }


        if (texture_faces  > 0) {
            triangle_texture_edge_a  = new int[texture_faces ];
            triangle_texture_edge_b  = new int[texture_faces ];
            triangle_texture_edge_c = new int[texture_faces ];
        }
        header.pos = vertexMod_offset;
        nc2.pos = vertexX_offset;
        nc3.pos = vertexY_offset;
        nc4.pos = vertexZ_offset;
        nc5.pos = vSkinBasePos;
        int start_x = 0;
        int start_y = 0;
        int start_z = 0;
        for (int point = 0; point < vertices ; point++) {
            int flag = header.readUByte();
            int x = 0;
            if ((flag & 1) != 0) {
                x = nc2.readSignedSmart();
            }
            int y = 0;
            if ((flag & 2) != 0) {
                y = nc3.readSignedSmart();
            }
            int z = 0;
            if ((flag & 4) != 0) {
                z = nc4.readSignedSmart();
            }
            vertex_x [point] = start_x + x;
            vertex_y[point] = start_y + y;
            vertex_z[point] = start_z + z;
            start_x = vertex_x [point];
            start_y =  vertex_y[point];
            start_z = vertex_z[point];
            if (bone_skin != null)
                bone_skin[point] = nc5.readUByte();

        }

        if (newVar == 1) { // HERE 1
            for (int var53 = 0; var53 < vertices ; ++var53) { // L: 204
                int var54 = nc5.readUByte(); // L: 205
                this.field2180[var53] = new int[var54]; // L: 206
                this.field2181[var53] = new int[var54]; // L: 207

                for (int var55 = 0; var55 < var54; ++var55) { // L: 208
                    this.field2180[var53][var55] = nc5.readUByte(); // L: 209
                    this.field2181[var53][var55] = nc5.readUByte(); // L: 210
                }
            }
        }

        header.pos = color_offset;
        nc2.pos = drawTypeBasePos;
        nc3.pos = facePriorityBasePos;
        nc4.pos = alphaBasePos;
        nc5.pos = tSkinBasePos;
        nc6.pos = textureIdBasePos;
        nc7.pos = textureBasePos;
        for (face = 0; face < faces ; face++) {
            face_color[face] = (short) header.readUShort();
            if (flags == 1) {
                render_type[face] = nc2.readSignedByte();
            }
            if (priority_opcode == 255) {
                face_render_priorities[face] = nc3.readSignedByte();
            }
            if (alpha_opcode == 1) {
                face_alpha[face] = nc4.readSignedByte();
                if (face_alpha[face] < 0)
                    face_alpha[face] = (byte) (256 + face_alpha[face]);

            }
            if (tSkin_opcode == 1)
                muscle_skin[face] = nc5.readUByte();

            if (texture_opcode == 1) {
                face_material[face] = (short) (nc6.readUShort() - 1);
                if(face_material[face] >= 0) {
                    if(render_type != null) {
                        if(render_type[face] < 2 &&  face_color[face] != 127 &&  face_color[face] != -27075) {
                            face_material[face] = -1;
                        }
                    }
                }
                if(face_material[face] != -1) {
                    face_color[face] = 127;
                }
            }
            if (face_texture  != null && face_material[face] != -1) {
                face_texture [face] = (byte) (nc7.readUByte() - 1);
            }
        }
        header.pos = faceVPoint_offset;
        nc2.pos = faceMeshLink_offset;
        int coordinate_a = 0;
        int coordinate_b = 0;
        int coordinate_c = 0;
        int last_coordinate = 0;
        for (face = 0; face < faces ; face++) {
            int opcode = nc2.readUByte();
            if (opcode == 1) {
                coordinate_a = header.readSignedSmart() + last_coordinate;
                last_coordinate = coordinate_a;
                coordinate_b = header.readSignedSmart() + last_coordinate;
                last_coordinate = coordinate_b;
                coordinate_c = header.readSignedSmart() + last_coordinate;
                last_coordinate = coordinate_c;
                triangle_edge_a [face] = (short) coordinate_a;
                triangle_edge_b[face] = (short) coordinate_b;
                triangle_edge_c[face] = (short) coordinate_c;
            }
            if (opcode == 2) {
                coordinate_b = coordinate_c;
                coordinate_c = header.readSignedSmart() + last_coordinate;
                last_coordinate = coordinate_c;
                triangle_edge_a [face] = (short) coordinate_a;
                triangle_edge_b[face] = (short) coordinate_b;
                triangle_edge_c[face] = (short) coordinate_c;
            }
            if (opcode == 3) {
                coordinate_a = coordinate_c;
                coordinate_c = header.readSignedSmart() + last_coordinate;
                last_coordinate = coordinate_c;
                triangle_edge_a [face] = (short) coordinate_a;
                triangle_edge_b[face] = (short) coordinate_b;
                triangle_edge_c[face] = (short) coordinate_c;
            }
            if (opcode == 4) {
                int l14 = coordinate_a;
                coordinate_a = coordinate_b;
                coordinate_b = l14;
                coordinate_c = header.readSignedSmart() + last_coordinate;
                last_coordinate = coordinate_c;
                triangle_edge_a [face] = (short) coordinate_a;
                triangle_edge_b[face] = (short) coordinate_b;
                triangle_edge_c[face] = (short) coordinate_c;
            }
        }
        header.pos = mainBuffer_offset;
        nc2.pos = firstBuffer_offset;
        nc3.pos = secondBuffer_offset;
        nc4.pos = thirdBuffer_offset;
        nc5.pos = fourthBuffer_offset;
        nc6.pos = fifthBuffer_offset;
        for (face = 0; face < texture_faces ; face++) {
            int opcode = texture_map[face] & 0xff;
            if (opcode == 0) {
                triangle_texture_edge_a [face] = (short) header.readUShort();
                triangle_texture_edge_b [face] = (short) header.readUShort();
                triangle_texture_edge_c[face] = (short) header.readUShort();
            }
            if (opcode == 1) {
                triangle_texture_edge_a [face] = (short) nc2.readUShort();
                triangle_texture_edge_b [face] = (short) nc2.readUShort();
                triangle_texture_edge_c[face] = (short) nc2.readUShort();
            }
            if (opcode == 2) {
                triangle_texture_edge_a [face] = (short) nc2.readUShort();
                triangle_texture_edge_b [face] = (short) nc2.readUShort();
                triangle_texture_edge_c[face] = (short) nc2.readUShort();
            }
            if (opcode == 3) {
                triangle_texture_edge_a [face] = (short) nc2.readUShort();
                triangle_texture_edge_b [face] = (short) nc2.readUShort();
                triangle_texture_edge_c[face] = (short) nc2.readUShort();
            }
        }
        header.pos = pos;
        face = header.readUByte();

        if(face != 0) {
            header.readUShort();
            header.readUShort();
            header.readUShort();
            header.getInt();
        }
    }


    private void decodeOld1(byte[] is) {
        boolean has_face_type = false;
        boolean has_texture_type = false;
        Buffer[] buffers = new Buffer[5];
        for(int i = 0; i < buffers.length; i++)
            buffers[i] = new Buffer(is);
        buffers[0].pos = is.length - 23;
        vertices  = buffers[0].readUShort();
        faces  = buffers[0].readUShort();
        texture_faces  = buffers[0].readUByte();
        int type_opcode = buffers[0].readUByte();
        int priority_opcode = buffers[0].readUByte();
        int alpha_opcode = buffers[0].readUByte();
        int tSkin_opcode = buffers[0].readUByte();
        int vSkin_opcode = buffers[0].readUByte();
        int nwvar =  buffers[0].readUByte();
        int i_162_ = buffers[0].readUShort();
        int i_163_ = buffers[0].readUShort();
        int i_164_ = buffers[0].readUShort();
        int i_165_ = buffers[0].readUShort();
        int var22 = buffers[0].readUShort();

        int pos = 0;
        int i_167_ = pos;

        pos += vertices ;
        int i_168_ = pos;
        pos += faces ;
        int priorityPos = pos;
        if(priority_opcode == 255)
            pos += faces ;
        int i_170_ = pos;
        if(tSkin_opcode == 1)
            pos += faces ;
        int i_171_ = pos;
        if(type_opcode == 1)
            pos += faces ;
        int i_172_ = pos;
        pos += var22;
        //vskin opcode shud be here
        int i_173_ = pos;
        if(alpha_opcode == 1)
            pos += faces ;
        int i_174_ = pos;
        pos += i_165_;
        int i_175_ = pos;
        pos += faces  * 2;
        int i_176_ = pos;
        pos += texture_faces  * 6;
        int i_177_ = pos;
        pos += i_162_;
        int i_178_ = pos;
        pos += i_163_;
        int i_179_ = pos;
        vertex_x  = new int[vertices ];
        vertex_y = new int[vertices ];
        vertex_z = new int[vertices ];
        triangle_edge_a  = new int[faces ];
        triangle_edge_b = new int[faces ];
        triangle_edge_c = new int[faces ];
        face_color = new int[faces ];

        if(vSkin_opcode == 1)
            bone_skin = new int[vertices ];

        if(tSkin_opcode == 1)
            muscle_skin = new int[faces ];

        if (nwvar == 1) { // L: 376
            this.field2180 = new int[vertices ][]; // L: 377
            this.field2181 = new int[vertices ][]; // L: 378
        }

        if(alpha_opcode == 1)
            face_alpha = new int[faces ];

        if(priority_opcode != 255)
            face_priority = (byte) priority_opcode;
        else
            face_render_priorities = new byte[faces];

        if(texture_faces  > 0) {
            triangle_texture_edge_a  = new int[texture_faces ];
            triangle_texture_edge_b  = new int[texture_faces ];
            triangle_texture_edge_c = new int[texture_faces ];
            texture_map = new byte[texture_faces ];
        }

        pos += i_164_;

        if(type_opcode == 1) {
            face_material = new int[faces ];
            render_type = new int[faces ];
            face_texture = new byte[faces];
        }

        buffers[0].pos = i_167_;
        buffers[1].pos = i_177_;
        buffers[2].pos = i_178_;
        buffers[3].pos = i_179_;
        buffers[4].pos = i_172_;
        int i_180_ = 0;
        int i_181_ = 0;
        int i_182_ = 0;
        for(int point = 0; vertices  > point; point++) {
            int flag = buffers[0].readUByte();
            int x = 0;
            if((flag & 0x1) != 0)
                x = buffers[1].readSignedSmart();
            int i_186_ = 0;
            if((0x2 & flag) != 0)
                i_186_ = buffers[2].readSignedSmart();
            int i_187_ = 0;
            if((flag & 0x4) != 0)
                i_187_ = buffers[3].readSignedSmart();
            vertex_x [point] = x + i_180_;
            vertex_y[point] = i_181_ + i_186_;
            vertex_z[point] = i_182_ + i_187_;
            i_180_ = vertex_x [point];
            i_181_ =  vertex_y[point];
            i_182_ = vertex_z[point];
            if(vSkin_opcode == 1) {
                int weight = buffers[4].readUByte();
                bone_skin[point] = weight;
            }
        }

        if (nwvar == 1) { // HERE 2
            for (int var40 = 0; var40 < vertices ; ++var40) { // L: 406
                int var41 = buffers[4].readUByte(); // L: 407
                this.field2180[var40] = new int[var41]; // L: 408
                this.field2181[var40] = new int[var41]; // L: 409

                for (int var42 = 0; var42 < var41; ++var42) { // L: 410
                    this.field2180[var40][var42] = buffers[4].readUByte(); // L: 411
                    this.field2181[var40][var42] = buffers[4].readUByte(); // L: 412
                }
            }
        }


        buffers[0].pos = i_175_;
        buffers[1].pos = i_171_;
        buffers[2].pos = priorityPos;
        buffers[3].pos = i_173_;
        buffers[4].pos = i_170_;
        for(int face = 0; face < faces ; face++) {
            face_color[face] = buffers[0].readUShort();
            if(type_opcode == 1) {
                int i_189_ = buffers[1].readUByte();
                if((i_189_ & 0x1) == 1) {
                    has_face_type = true;
                    render_type[face] = (byte) 1;
                } else
                    render_type[face] = (byte) 0;
                if((i_189_ & 0x2) == 2) {
                    face_texture[face] = (byte) (i_189_ >> 2);
                    face_material[face] =  face_color[face];
                    face_color[face] = 127;
                    if(face_material[face] != -1)
                        has_texture_type = true;
                } else {
                    face_texture[face] = (short) -1;
                    face_material[face] = (short) -1;
                }
            }
            if(priority_opcode == 255)
                face_render_priorities[face] = buffers[2].readSignedByte();
            if(alpha_opcode == 1) {
                face_alpha[face] = buffers[3].readSignedByte();
                if (face_alpha[face] < 0)
                    face_alpha[face] = (byte) (256 + face_alpha[face]);
            }
            if(tSkin_opcode == 1)
                muscle_skin[face] = buffers[4].readUByte();
        }
        int used_vertexAmt = -1;
        buffers[0].pos = i_174_;
        buffers[1].pos = i_168_;
        short i_190_ = 0;
        short i_191_ = 0;
        short i_192_ = 0;
        int i_193_ = 0;
        for(int i_194_ = 0; faces  > i_194_; i_194_++) {
            int i_195_ = buffers[1].readUByte();
            if(i_195_ == 1) {
                i_190_ = (short) (i_193_ + buffers[0].readSignedSmart());
                i_193_ = i_190_;
                i_191_ = (short) (i_193_ + buffers[0].readSignedSmart());
                i_193_ = i_191_;
                i_192_ = (short) (buffers[0].readSignedSmart() + i_193_);
                i_193_ = i_192_;
                triangle_edge_a [i_194_] = i_190_;
                triangle_edge_b[i_194_] = i_191_;
                triangle_edge_c[i_194_] = i_192_;
                if(used_vertexAmt < i_190_)
                    used_vertexAmt = i_190_;
                if((i_191_ ^ 0xffffffff) < (used_vertexAmt ^ 0xffffffff))
                    used_vertexAmt = i_191_;
                if(i_192_ > used_vertexAmt)
                    used_vertexAmt = i_192_;
            }
            if(i_195_ == 2) {
                i_191_ = i_192_;
                i_192_ = (short) (i_193_ + buffers[0].readSignedSmart());
                i_193_ = i_192_;
                triangle_edge_a [i_194_] = i_190_;
                triangle_edge_b[i_194_] = i_191_;
                triangle_edge_c[i_194_] = i_192_;
                if(used_vertexAmt < i_192_)
                    used_vertexAmt = i_192_;
            }
            if(i_195_ == 3) {
                i_190_ = i_192_;
                i_192_ = (short) (buffers[0].readSignedSmart() + i_193_);
                i_193_ = i_192_;
                triangle_edge_a [i_194_] = i_190_;
                triangle_edge_b[i_194_] = i_191_;
                triangle_edge_c[i_194_] = i_192_;
                if((i_192_ ^ 0xffffffff) < (used_vertexAmt ^ 0xffffffff))
                    used_vertexAmt = i_192_;
            }
            if(i_195_ == 4) {
                short i_196_ = i_190_;
                i_190_ = i_191_;
                i_191_ = i_196_;
                i_192_ = (short) (buffers[0].readSignedSmart() + i_193_);
                i_193_ = i_192_;
                triangle_edge_a [i_194_] = i_190_;
                triangle_edge_b[i_194_] = i_191_;
                triangle_edge_c[i_194_] = i_192_;
                if((~i_192_) < (~used_vertexAmt))
                    used_vertexAmt = i_192_;
            }
        }
        buffers[0].pos = i_176_;
        used_vertexAmt++;
        for(int i_197_ = 0; texture_faces  > i_197_; i_197_++) {
            texture_map[i_197_] = (byte) 0;
            triangle_texture_edge_a [i_197_] = (short) buffers[0].readUShort();
            triangle_texture_edge_b [i_197_] = (short) buffers[0].readUShort();
            triangle_texture_edge_c[i_197_] = (short) buffers[0].readUShort();
        }
        if(face_texture != null) {
            boolean textured = false;
            for(int i_199_ = 0; i_199_ < faces ; i_199_++) {
                int i_200_ = 0xffff & face_texture[i_199_];
                if(i_200_ != 0xffff) {
                    if(((triangle_texture_edge_a [i_200_] & 0xffff) == triangle_edge_a [i_199_]) && ((0xffff & triangle_texture_edge_b [i_200_]) == triangle_edge_b[i_199_]) && triangle_edge_c[i_199_] == ( triangle_texture_edge_c[i_200_] & 0xffff))
                        face_texture[i_199_] = (byte) -1;
                    else
                        textured = true;
                }
            }
            if(!textured)
                face_texture = null;
        }
        if(!has_texture_type) {
            face_material = null;
        }
        if(!has_face_type)
            render_type = null;
        int s = 4 << 7;
    }
    private Model(int modelId) {
       byte[] data = aClass21Array1661[modelId].data;
        if (data[data.length - 1] == -3 && data[data.length - 2] == -1) { // L: 66
          decodeOSRS1(data);
        } else if (data[data.length - 1] == -2 && data[data.length - 2] == -1) { // L: 67
          decodeOld1(data);
        } else if (data[data.length - 1] == -1 && data[data.length - 2] == -1) { // usesNewHeader
            readNewModel(data, modelId);//decodetype1
        } else {
            readOldModel(data, modelId);
        }
        repeatTexture = new boolean[faces];

        if (repeatedTextureModels.contains(modelId)) {
            Arrays.fill(repeatTexture, true);
        }
    }
//public Model(int modelId) {
//    byte[] data = aClass21Array1661[modelId].data;
//    if (data[data.length - 1] == -1 && data[data.length - 2] == -1) {
//        readNewModel(data, modelId);
//    } else {
//        readOldModel(data, modelId);
//    }
//
//    repeatTexture = new boolean[faces];
//
//    if (repeatedTextureModels.contains(modelId)) {
//        Arrays.fill(repeatTexture, true);
//    }
//}
    public boolean[] repeatTexture;

    private Model(boolean flag) {
        aBoolean1618 = true;
        within_tile = false;
        if (!flag)
            aBoolean1618 = !aBoolean1618;
    }

    public Model(int length, Model model_segments[], boolean preset) {
        try {
            aBoolean1618 = true;
            within_tile = false;
            anInt1620++;
            boolean render_type_flag = false;
            boolean priority_flag = false;
            boolean alpha_flag = false;
            boolean muscle_skin_flag = false;
            boolean color_flag = false;
            boolean texture_flag = false;
            boolean coordinate_flag = false;
            vertices = 0;
            faces = 0;
            texture_faces = 0;
            face_priority = -1;
            Model build;
            for (int segment_index = 0; segment_index < length; segment_index++) {
                build = model_segments[segment_index];
                if (build != null) {
                    vertices += build.vertices;
                    faces += build.faces;
                    texture_faces += build.texture_faces;
                    if (build.face_render_priorities != null) {
                        priority_flag = true;
                    } else {
                        if (face_priority == -1)
                            face_priority = build.face_priority;

                        if (face_priority != build.face_priority)
                            priority_flag = true;
                    }
                    render_type_flag |= build.render_type != null;
                    alpha_flag |= build.face_alpha != null;
                    muscle_skin_flag |= build.muscle_skin != null;
                    color_flag |= build.face_color != null;
                    texture_flag |= build.face_material != null;
                    coordinate_flag |= build.face_texture != null;
                }
            }
            vertex_x = new int[vertices];
            vertex_y = new int[vertices];
            vertex_z = new int[vertices];
            bone_skin = new int[vertices];
            triangle_edge_a = new int[faces];
            triangle_edge_b = new int[faces];
            triangle_edge_c = new int[faces];
            repeatTexture = new boolean[faces];
            if (color_flag) {
                face_color = new int[faces];
                repeatTexture = new boolean[faces];
            }

            if (render_type_flag)
                render_type = new int[faces];

            if (priority_flag)
                face_render_priorities = new byte[faces];

            if (alpha_flag)
                face_alpha = new int[faces];

            if (muscle_skin_flag)
                muscle_skin = new int[faces];

            if (texture_flag)
                face_material = new int[faces];

            if (coordinate_flag)
                face_texture = new byte[faces];

            if (texture_faces > 0) {
                texture_map = new byte[texture_faces];
                triangle_texture_edge_a = new int[texture_faces];
                triangle_texture_edge_b = new int[texture_faces];
                triangle_texture_edge_c = new int[texture_faces];
            }

            vertices = 0;
            faces = 0;
            texture_faces = 0;
            for (int segment_index = 0; segment_index < length; segment_index++) {
                build = model_segments[segment_index];
                if (build != null) {
                    for (int face = 0; face < build.faces; face++) {
                        if (render_type_flag && build.render_type != null)
                            render_type[faces] = build.render_type[face];

                        if (priority_flag)
                            if (build.face_render_priorities == null)
                                face_render_priorities[faces] = build.face_priority;
                            else
                                face_render_priorities[faces] = build.face_render_priorities[face];

                        if (alpha_flag && build.face_alpha != null)
                            face_alpha[faces] = build.face_alpha[face];

                        if (muscle_skin_flag && build.muscle_skin != null)
                            muscle_skin[faces] = build.muscle_skin[face];

                        if (texture_flag) {
                            if (build.face_material != null)
                                face_material[faces] = build.face_material[face];
                            else
                                face_material[faces] = -1;
                        }
                        if (coordinate_flag) {
                            if (build.face_texture != null && build.face_texture[face] != -1) {
                                face_texture[faces] = (byte) (build.face_texture[face] + texture_faces);
                            } else {
                                face_texture[faces] = -1;
                            }
                        }

                        if (color_flag && build.face_color != null)
                            face_color[faces] = build.face_color[face];
                        repeatTexture[faces] = build.repeatTexture[face];

                        triangle_edge_a[faces] = method465(build, build.triangle_edge_a[face]);
                        triangle_edge_b[faces] = method465(build, build.triangle_edge_b[face]);
                        triangle_edge_c[faces] = method465(build, build.triangle_edge_c[face]);
                        faces++;
                    }
                    for (int texture_edge = 0; texture_edge < build.texture_faces; texture_edge++) {
                        byte opcode = texture_map[texture_faces] = build.texture_map[texture_edge];
                        if (opcode == 0) {
                            triangle_texture_edge_a[texture_faces] = (short) method465(build, build.triangle_texture_edge_a[texture_edge]);
                            triangle_texture_edge_b[texture_faces] = (short) method465(build, build.triangle_texture_edge_b[texture_edge]);
                            triangle_texture_edge_c[texture_faces] = (short) method465(build, build.triangle_texture_edge_c[texture_edge]);
                        }
                        if (opcode >= 1 && opcode <= 3) {
                            triangle_texture_edge_a[texture_faces] = build.triangle_texture_edge_a[texture_edge];
                            triangle_texture_edge_b[texture_faces] = build.triangle_texture_edge_b[texture_edge];
                            triangle_texture_edge_c[texture_faces] = build.triangle_texture_edge_c[texture_edge];
                        }
                        if (opcode == 2) {

                        }
                        texture_faces++;
                    }
                    if (!preset) //for models that don't have preset textured_faces
                        texture_faces++;

                }
            }
        } catch (Exception e) {
            System.out.println(e);
        }
    }

    public Model(Model amodel[]) {
        int i = 2;
        aBoolean1618 = true;
        within_tile = false;
        anInt1620++;
        boolean flag1 = false;
        boolean flag2 = false;
        boolean flag3 = false;
        boolean flag4 = false;
        boolean texture_flag = false;
        boolean coordinate_flag = false;
        vertices = 0;
        faces = 0;
        texture_faces = 0;
        face_priority = -1;
        for (int k = 0; k < i; k++) {
            Model model = amodel[k];
            if (model != null) {
                vertices += model.vertices;
                faces += model.faces;
                texture_faces += model.texture_faces;
                flag1 |= model.render_type != null;
                if (model.face_render_priorities != null) {
                    flag2 = true;
                } else {
                    if (face_priority == -1)
                        face_priority = model.face_priority;
                    if (face_priority != model.face_priority)
                        flag2 = true;
                }
                flag3 |= model.face_alpha != null;
                flag4 |= model.face_color != null;
                texture_flag |= model.face_material != null;
                coordinate_flag |= model.face_texture != null;
            }
        }

        vertex_x = new int[vertices];
        vertex_y = new int[vertices];
        vertex_z = new int[vertices];
        triangle_edge_a = new int[faces];
        triangle_edge_b = new int[faces];
        triangle_edge_c = new int[faces];
        faceHslA = new int[faces];
        faceHslB = new int[faces];
        faceHslC = new int[faces];
        triangle_texture_edge_a = new int[texture_faces];
        triangle_texture_edge_b = new int[texture_faces];
        triangle_texture_edge_c = new int[texture_faces];
        if (flag1)
            render_type = new int[faces];
        if (flag2)
            face_render_priorities = new byte[faces];
        if (flag3)
            face_alpha = new int[faces];
        if (flag4) {
            face_color = new int[faces];
            repeatTexture = new boolean[faces];
        }
        if (texture_flag)
            face_material = new int[faces];

        if (coordinate_flag)
            face_texture = new byte[faces];
        vertices = 0;
        faces = 0;
        texture_faces = 0;
        int i1 = 0;
        for (int j1 = 0; j1 < i; j1++) {
            Model model_1 = amodel[j1];
            if (model_1 != null) {
                int k1 = vertices;
                for (int l1 = 0; l1 < model_1.vertices; l1++) {
                    int x = model_1.vertex_x[l1];
                    int y = model_1.vertex_y[l1];
                    int z = model_1.vertex_z[l1];
                    vertex_x[vertices] = x;
                    vertex_y[vertices] = y;
                    vertex_z[vertices] = z;
                    ++vertices;
                }

                for (int uid = 0; uid < model_1.faces; uid++) {
                    triangle_edge_a[faces] = model_1.triangle_edge_a[uid] + k1;
                    triangle_edge_b[faces] = model_1.triangle_edge_b[uid] + k1;
                    triangle_edge_c[faces] = model_1.triangle_edge_c[uid] + k1;
                    faceHslA[faces] = model_1.faceHslA[uid];
                    faceHslB[faces] = model_1.faceHslB[uid];
                    faceHslC[faces] = model_1.faceHslC[uid];
                    if (flag1)
                        if (model_1.render_type == null) {
                            render_type[faces] = 0;
                        } else {
                            int j2 = model_1.render_type[uid];
                            if ((j2 & 2) == 2)
                                j2 += i1 << 2;
                            render_type[faces] = j2;
                        }
                    if (flag2)
                        if (model_1.face_render_priorities == null)
                            face_render_priorities[faces] = model_1.face_priority;
                        else
                            face_render_priorities[faces] = model_1.face_render_priorities[uid];
                    if (flag3) {
                        if (model_1.face_alpha == null)
                            face_alpha[faces] = 0;
                        else
                            face_alpha[faces] = model_1.face_alpha[uid];

                    }
                    if (flag4 && model_1.face_color != null) {
                        face_color[faces] = model_1.face_color[uid];
                        repeatTexture[faces] = model_1.repeatTexture[uid];
                    }

                    if (texture_flag) {
                        if (model_1.face_material != null) {
                            face_material[faces] = model_1.face_material[faces];
                        } else {
                            face_material[faces] = -1;
                        }
                    }

                    if (coordinate_flag) {
                        if (model_1.face_texture != null && model_1.face_texture[faces] != -1)
                            face_texture[faces] = (byte) (model_1.face_texture[faces] + texture_faces);
                        else
                            face_texture[faces] = -1;

                    }

                    faces++;
                }

                for (int k2 = 0; k2 < model_1.texture_faces; k2++) {
                    triangle_texture_edge_a[texture_faces] = (short) (model_1.triangle_texture_edge_a[k2] + k1);
                    triangle_texture_edge_b[texture_faces] = (short) (model_1.triangle_texture_edge_b[k2] + k1);
                    triangle_texture_edge_c[texture_faces] = (short) (model_1.triangle_texture_edge_c[k2] + k1);
                    texture_faces++;
                }

                i1 += model_1.texture_faces;
            }
        }

        calc_diagonals();
    }

    public Model(boolean color_flag, boolean alpha_flag, boolean animated, Model model) {
        this(color_flag, alpha_flag, animated, false, model);
    }

    public Model(boolean color_flag, boolean alpha_flag, boolean animated, boolean texture_flag, Model model) {
        aBoolean1618 = true;
        within_tile = false;
        anInt1620++;
        vertices = model.vertices;
        faces = model.faces;
        texture_faces = model.texture_faces;
        if (animated) {
            vertex_x = model.vertex_x;
            vertex_y = model.vertex_y;
            vertex_z = model.vertex_z;
        } else {
            vertex_x = new int[vertices];
            vertex_y = new int[vertices];
            vertex_z = new int[vertices];
            for (int point = 0; point < vertices; point++) {
                vertex_x[point] = model.vertex_x[point];
                vertex_y[point] = model.vertex_y[point];
                vertex_z[point] = model.vertex_z[point];
            }

        }

        if (color_flag) {
            face_color = model.face_color;
            repeatTexture = model.repeatTexture;
        } else {
            face_color = new int[faces];
            repeatTexture = new boolean[faces];
            for (int face = 0; face < faces; face++) {
                face_color[face] = model.face_color[face];
                repeatTexture[face] = model.repeatTexture[face];
            }

        }

        if(!texture_flag && model.face_material != null) {
            face_material = new int[faces];
            for(int face = 0; face < faces; face++) {
                face_material[face] = model.face_material[face];
            }
        } else {
            face_material = model.face_material;
        }

        if (alpha_flag) {
            face_alpha = model.face_alpha;
        } else {
            face_alpha = new int[faces];
            if (model.face_alpha == null) {
                for (int l = 0; l < faces; l++)
                    face_alpha[l] = 0;

            } else {
                for (int i1 = 0; i1 < faces; i1++)
                    face_alpha[i1] = model.face_alpha[i1];

            }
        }
        bone_skin = model.bone_skin;
        muscle_skin = model.muscle_skin;
        render_type = model.render_type;
        triangle_edge_a = model.triangle_edge_a;
        triangle_edge_b = model.triangle_edge_b;
        triangle_edge_c = model.triangle_edge_c;
        face_render_priorities = model.face_render_priorities;
        face_texture = model.face_texture;
        texture_map = model.texture_map;
        face_priority = model.face_priority;
        triangle_texture_edge_a = model.triangle_texture_edge_a;
        triangle_texture_edge_b = model.triangle_texture_edge_b;
        triangle_texture_edge_c = model.triangle_texture_edge_c;
    }

    public Model(boolean adjust_elevation, boolean gouraud_shading, Model model) {
        aBoolean1618 = true;
        within_tile = false;
        anInt1620++;
        vertices = model.vertices;
        faces = model.faces;
        texture_faces = model.texture_faces;
        if (adjust_elevation) {
            vertex_y = new int[vertices];
            for (int point = 0; point < vertices; point++)
                vertex_y[point] = model.vertex_y[point];

        } else {
            vertex_y = model.vertex_y;
        }
        if (gouraud_shading) {
            faceHslA = new int[faces];
            faceHslB = new int[faces];
            faceHslC = new int[faces];
            for (int face = 0; face < faces; face++) {
                faceHslA[face] = model.faceHslA[face];
                faceHslB[face] = model.faceHslB[face];
                faceHslC[face] = model.faceHslC[face];
            }

            render_type = new int[faces];
            if (model.render_type == null) {
                for (int face = 0; face < faces; face++)
                    render_type[face] = 0;

            } else {
                for (int face = 0; face < faces; face++)
                    render_type[face] = model.render_type[face];

            }
            super.normals = new Vertex[vertices];
            for (int point = 0; point < vertices; point++) {
                Vertex class33 = super.normals[point] = new Vertex();
                Vertex class33_1 = model.normals[point];
                class33.x = class33_1.x;
                class33.y = class33_1.y;
                class33.z = class33_1.z;
                class33.magnitude = class33_1.magnitude;
            }
            gouraud_vertex = model.gouraud_vertex;

        } else {
            faceHslA = model.faceHslA;
            faceHslB = model.faceHslB;
            faceHslC = model.faceHslC;
            render_type = model.render_type;
        }
        vertex_x = model.vertex_x;
        vertex_z = model.vertex_z;
        triangle_edge_a = model.triangle_edge_a;
        triangle_edge_b = model.triangle_edge_b;
        triangle_edge_c = model.triangle_edge_c;
        face_render_priorities = model.face_render_priorities;
        face_alpha = model.face_alpha;
        face_texture = model.face_texture;
        face_color = model.face_color;
        repeatTexture = model.repeatTexture;
        face_material = model.face_material;
        face_priority = model.face_priority;
        texture_map = model.texture_map;
        triangle_texture_edge_a = model.triangle_texture_edge_a;
        triangle_texture_edge_b = model.triangle_texture_edge_b;
        triangle_texture_edge_c = model.triangle_texture_edge_c;
        super.model_height = model.model_height;
        diagonal_2D = model.diagonal_2D;
        diagonal_3D = model.diagonal_3D;
        scene_depth = model.scene_depth;
        min_x = model.min_x;
        max_z = model.max_z;
        min_z = model.min_z;
        max_x = model.max_x;
    }

    public static void clear() {
        aClass21Array1661 = null;
        hasAnEdgeToRestrict = null;
        outOfReach = null;
        projected_vertex_y = null;
        projected_vertex_z = null;
        camera_vertex_x = null;
        camera_vertex_y = null;
        camera_vertex_z = null;
        depthListIndices = null;
        faceLists = null;
        anIntArray1673 = null;
        anIntArrayArray1674 = null;
        anIntArray1675 = null;
        anIntArray1676 = null;
        anIntArray1677 = null;
        SINE = null;
        COSINE = null;
        modelIntArray3 = null;
        modelIntArray4 = null;
    }

    public static void method460(byte abyte0[], int j) {
        try {
            if (abyte0 == null) {
                ModelHeader class21 = aClass21Array1661[j] = new ModelHeader();
                class21.vertices = 0;
                class21.faces = 0;
                class21.texture_faces = 0;
                return;
            }
            Buffer stream = new Buffer(abyte0);
            stream.pos = abyte0.length - 18;
            ModelHeader class21_1 = aClass21Array1661[j] = new ModelHeader();
            class21_1.data = abyte0;
            class21_1.vertices = stream.readUShort();
            class21_1.faces = stream.readUShort();
            class21_1.texture_faces = stream.readUByte();
            int k = stream.readUByte();
            int l = stream.readUByte();
            int i1 = stream.readUByte();
            int j1 = stream.readUByte();
            int k1 = stream.readUByte();
            int l1 = stream.readUShort();
            int uid = stream.readUShort();
            int j2 = stream.readUShort();
            int k2 = stream.readUShort();
            int l2 = 0;
            class21_1.vertex_offset = l2;
            l2 += class21_1.vertices;
            class21_1.face_offset = l2;
            l2 += class21_1.faces;
            class21_1.face_pri_offset = l2;
            if (l == 255)
                l2 += class21_1.faces;
            else
                class21_1.face_pri_offset = -l - 1;
            class21_1.muscle_offset = l2;
            if (j1 == 1)
                l2 += class21_1.faces;
            else
                class21_1.muscle_offset = -1;
            class21_1.render_type_offset = l2;
            if (k == 1)
                l2 += class21_1.faces;
            else
                class21_1.muscle_offset = -1;
            class21_1.bones_offset = l2;
            if (k1 == 1)
                l2 += class21_1.vertices;
            else
                class21_1.bones_offset = -1;
            class21_1.alpha_offset = l2;
            if (i1 == 1)
                l2 += class21_1.faces;
            else
                class21_1.alpha_offset = -1;
            class21_1.points_offset = l2;
            l2 += k2;
            class21_1.color_id = l2;
            l2 += class21_1.faces * 2;
            class21_1.texture_id = l2;
            l2 += class21_1.texture_faces * 6;
            class21_1.vertex_x_offset = l2;
            l2 += l1;
            class21_1.vertex_y_offset = l2;
            l2 += uid;
            class21_1.vertex_z_offset = l2;
            l2 += j2;
        } catch (Exception _ex) {
        }
    }

    public static void method459(int id, Provider onDemandFetcherParent) {
        aClass21Array1661 = new ModelHeader[80000]; //TODO: should this be id?
        resourceProvider = onDemandFetcherParent;
    }

    public static void method461(int file) {
        aClass21Array1661[file] = null;
    }

    public static Model get(int file) {
        if (aClass21Array1661 == null)
            return null;

        ModelHeader class21 = aClass21Array1661[file];
        if (class21 == null) {
            resourceProvider.provide(file);
            return null;
        } else {
            return new Model(file);
        }
    }

    public static boolean cached(int file) {
        if (aClass21Array1661 == null)
            return false;
        //This is great debugging, we can see where the file is set to 65535 in the stack trace when doing this.
        //if (file == 65535) throw new RuntimeException();
        ModelHeader class21 = aClass21Array1661[file];
        if (class21 == null) {
            resourceProvider.provide(file);
            return false;
        } else {
            return true;
        }
    }

    public void readOldModel(byte[] data, int modelId) {
        boolean has_face_type = false;
        boolean has_texture_type = false;
        Buffer stream = new Buffer(data);
        Buffer stream1 = new Buffer(data);
        Buffer stream2 = new Buffer(data);
        Buffer stream3 = new Buffer(data);
        Buffer stream4 = new Buffer(data);
        stream.pos = data.length - 18;
        vertices = stream.readUShort();
        faces = stream.readUShort();
        texture_faces = stream.readUByte();
        int type_opcode = stream.readUByte();
        int priority_opcode = stream.readUByte();
        int alpha_opcode = stream.readUByte();
        int tSkin_opcode = stream.readUByte();
        int vSkin_opcode = stream.readUByte();
        int i_254_ = stream.readUShort();
        int i_255_ = stream.readUShort();
        int i_256_ = stream.readUShort();
        int i_257_ = stream.readUShort();
        int i_258_ = 0;

        int i_259_ = i_258_;
        i_258_ += vertices;

        int i_260_ = i_258_;
        i_258_ += faces;

        int i_261_ = i_258_;
        if (priority_opcode == 255)
            i_258_ += faces;

        int i_262_ = i_258_;
        if (tSkin_opcode == 1)
            i_258_ += faces;

        int i_263_ = i_258_;
        if (type_opcode == 1)
            i_258_ += faces;

        int i_264_ = i_258_;
        if (vSkin_opcode == 1)
            i_258_ += vertices;

        int i_265_ = i_258_;
        if (alpha_opcode == 1)
            i_258_ += faces;

        int i_266_ = i_258_;
        i_258_ += i_257_;

        int i_267_ = i_258_;
        i_258_ += faces * 2;

        int i_268_ = i_258_;
        i_258_ += texture_faces * 6;

        int i_269_ = i_258_;
        i_258_ += i_254_;

        int i_270_ = i_258_;
        i_258_ += i_255_;

        int i_271_ = i_258_;
        i_258_ += i_256_;

        vertex_x = new int[vertices];
        vertex_y = new int[vertices];
        vertex_z = new int[vertices];
        triangle_edge_a = new int[faces];
        triangle_edge_b = new int[faces];
        triangle_edge_c = new int[faces];
        if (texture_faces > 0) {
            texture_map = new byte[texture_faces];
            triangle_texture_edge_a = new int[texture_faces];
            triangle_texture_edge_b = new int[texture_faces];
            triangle_texture_edge_c = new int[texture_faces];
        }

        if (vSkin_opcode == 1)
            bone_skin = new int[vertices];

        if (type_opcode == 1) {
            render_type = new int[faces];
            face_texture = new byte[faces];
            face_material = new int[faces];
        }

        if (priority_opcode == 255)
            face_render_priorities = new byte[faces];
        else
            face_priority = (byte) priority_opcode;

        if (alpha_opcode == 1)
            face_alpha = new int[faces];

        if (tSkin_opcode == 1)
            muscle_skin = new int[faces];

        face_color = new int[faces];
        repeatTexture = new boolean[faces];
        stream.pos = i_259_;
        stream1.pos = i_269_;
        stream2.pos = i_270_;
        stream3.pos = i_271_;
        stream4.pos = i_264_;
        int start_x = 0;
        int start_y = 0;
        int start_z = 0;
        for (int point = 0; point < vertices; point++) {
            int flag = stream.readUByte();
            int x = 0;
            if ((flag & 0x1) != 0)
                x = stream1.readSignedSmart();
            int y = 0;
            if ((flag & 0x2) != 0)
                y = stream2.readSignedSmart();
            int z = 0;
            if ((flag & 0x4) != 0)
                z = stream3.readSignedSmart();

            vertex_x[point] = start_x + x;
            vertex_y[point] = start_y + y;
            vertex_z[point] = start_z + z;
            start_x = vertex_x[point];
            start_y = vertex_y[point];
            start_z = vertex_z[point];
            if (vSkin_opcode == 1)
                bone_skin[point] = stream4.readUByte();

        }
        stream.pos = i_267_;
        stream1.pos = i_263_;
        stream2.pos = i_261_;
        stream3.pos = i_265_;
        stream4.pos = i_262_;
        for (int face = 0; face < faces; face++) {
            face_color[face] = (short) stream.readUShort();
            if (type_opcode == 1) {
                int flag = stream1.readUByte();
                if ((flag & 0x1) == 1) {
                    render_type[face] = 1;
                    has_face_type = true;
                } else {
                    render_type[face] = 0;
                }

                if ((flag & 0x2) != 0) {
                    face_texture[face] = (byte) (flag >> 2);
                    face_material[face] = face_color[face];
                    face_color[face] = 127;
                    if (face_material[face] != -1)
                        has_texture_type = true;
                } else {
                    face_texture[face] = -1;
                    face_material[face] = -1;
                }
            }
            if (priority_opcode == 255)
                face_render_priorities[face] = stream2.readSignedByte();

            if (alpha_opcode == 1) {
                face_alpha[face] = stream3.readSignedByte();
                if (face_alpha[face] < 0)
                    face_alpha[face] = (256 + face_alpha[face]);

            }
            if (tSkin_opcode == 1)
                muscle_skin[face] = stream4.readUByte();

        }
        stream.pos = i_266_;
        stream1.pos = i_260_;
        int coordinate_a = 0;
        int coordinate_b = 0;
        int coordinate_c = 0;
        int offset = 0;
        int coordinate;
        for (int face = 0; face < faces; face++) {
            int opcode = stream1.readUByte();
            if (opcode == 1) {
                coordinate_a = (stream.readSignedSmart() + offset);
                offset = coordinate_a;
                coordinate_b = (stream.readSignedSmart() + offset);
                offset = coordinate_b;
                coordinate_c = (stream.readSignedSmart() + offset);
                offset = coordinate_c;
                triangle_edge_a[face] = coordinate_a;
                triangle_edge_b[face] = coordinate_b;
                triangle_edge_c[face] = coordinate_c;
            }
            if (opcode == 2) {
                coordinate_b = coordinate_c;
                coordinate_c = (stream.readSignedSmart() + offset);
                offset = coordinate_c;
                triangle_edge_a[face] = coordinate_a;
                triangle_edge_b[face] = coordinate_b;
                triangle_edge_c[face] = coordinate_c;
            }
            if (opcode == 3) {
                coordinate_a = coordinate_c;
                coordinate_c = (stream.readSignedSmart() + offset);
                offset = coordinate_c;
                triangle_edge_a[face] = coordinate_a;
                triangle_edge_b[face] = coordinate_b;
                triangle_edge_c[face] = coordinate_c;
            }
            if (opcode == 4) {
                coordinate = coordinate_a;
                coordinate_a = coordinate_b;
                coordinate_b = coordinate;
                coordinate_c = (stream.readSignedSmart() + offset);
                offset = coordinate_c;
                triangle_edge_a[face] = coordinate_a;
                triangle_edge_b[face] = coordinate_b;
                triangle_edge_c[face] = coordinate_c;
            }
        }
        stream.pos = i_268_;
        for (int face = 0; face < texture_faces; face++) {
            texture_map[face] = 0;
            triangle_texture_edge_a[face] = (short) stream.readUShort();
            triangle_texture_edge_b[face] = (short) stream.readUShort();
            triangle_texture_edge_c[face] = (short) stream.readUShort();
        }
        if (face_texture != null) {
            boolean textured = false;
            for (int face = 0; face < faces; face++) {
                coordinate = face_texture[face] & 0xff;
                if (coordinate != 255) {
                    if (((triangle_texture_edge_a[coordinate] & 0xffff) == triangle_edge_a[face]) && ((triangle_texture_edge_b[coordinate] & 0xffff) == triangle_edge_b[face]) && ((triangle_texture_edge_c[coordinate] & 0xffff) == triangle_edge_c[face])) {
                        face_texture[face] = -1;
                    } else {
                        textured = true;
                    }
                }
            }
            if (!textured)
                face_texture = null;
        }
        if (!has_texture_type)
            face_material = null;

        if (!has_face_type)
            render_type = null;

        repeatTexture = new boolean[faces];

    }

    public void readNewModel(byte[] data, int modelId) {
        Buffer nc1 = new Buffer(data);
        Buffer nc2 = new Buffer(data);
        Buffer nc3 = new Buffer(data);
        Buffer nc4 = new Buffer(data);
        Buffer nc5 = new Buffer(data);
        Buffer nc6 = new Buffer(data);
        Buffer nc7 = new Buffer(data);
        nc1.pos = data.length - 23;
        vertices = nc1.readUShort();
        faces = nc1.readUShort();
        texture_faces = nc1.readUByte();
        int flags = nc1.readUByte();
        int priority_opcode = nc1.readUByte();
        int alpha_opcode = nc1.readUByte();
        int tSkin_opcode = nc1.readUByte();
        int texture_opcode = nc1.readUByte();
        int vSkin_opcode = nc1.readUByte();
        int j3 = nc1.readUShort();
        int k3 = nc1.readUShort();
        int l3 = nc1.readUShort();
        int i4 = nc1.readUShort();
        int j4 = nc1.readUShort();
        int texture_id = 0;
        int texture_ = 0;
        int texture__ = 0;
        int face;
        face_color = new int[faces];
        repeatTexture = new boolean[faces];
        if (texture_faces > 0) {
            texture_map = new byte[texture_faces];
            nc1.pos = 0;
            for (face = 0; face < texture_faces; face++) {
                byte opcode = texture_map[face] = nc1.readSignedByte();
                if (opcode == 0) {
                    texture_id++;
                }

                if (opcode >= 1 && opcode <= 3) {
                    texture_++;
                }
                if (opcode == 2) {
                    texture__++;
                }
            }
        }
        int pos;
        pos = texture_faces;
        int vertexMod_offset = pos;
        pos += vertices;

        int drawTypeBasePos = pos;
        if (flags == 1)
            pos += faces;

        int faceMeshLink_offset = pos;
        pos += faces;

        int facePriorityBasePos = pos;
        if (priority_opcode == 255)
            pos += faces;

        int tSkinBasePos = pos;
        if (tSkin_opcode == 1)
            pos += faces;

        int vSkinBasePos = pos;
        if (vSkin_opcode == 1)
            pos += vertices;

        int alphaBasePos = pos;
        if (alpha_opcode == 1)
            pos += faces;

        int faceVPoint_offset = pos;
        pos += i4;

        int textureIdBasePos = pos;
        if (texture_opcode == 1)
            pos += faces * 2;

        int textureBasePos = pos;
        pos += j4;

        int color_offset = pos;
        pos += faces * 2;

        int vertexX_offset = pos;
        pos += j3;

        int vertexY_offset = pos;
        pos += k3;

        int vertexZ_offset = pos;
        pos += l3;

        int mainBuffer_offset = pos;
        pos += texture_id * 6;

        int firstBuffer_offset = pos;
        pos += texture_ * 6;

        int secondBuffer_offset = pos;
        pos += texture_ * 6;

        int thirdBuffer_offset = pos;
        pos += texture_ * 2;

        int fourthBuffer_offset = pos;
        pos += texture_;

        int fifthBuffer_offset = pos;
        pos += texture_ * 2 + texture__ * 2;

        vertex_x = new int[vertices];
        vertex_y = new int[vertices];
        vertex_z = new int[vertices];
        triangle_edge_a = new int[faces];
        triangle_edge_b = new int[faces];
        triangle_edge_c = new int[faces];
        if (vSkin_opcode == 1)
            bone_skin = new int[vertices];

        if (flags == 1)
            render_type = new int[faces];

        if (priority_opcode == 255)
            face_render_priorities = new byte[faces];
        else
            face_priority = (byte) priority_opcode;

        if (alpha_opcode == 1)
            face_alpha = new int[faces];

        if (tSkin_opcode == 1)
            muscle_skin = new int[faces];

        if (texture_opcode == 1)
            face_material = new int[faces];

        if (texture_opcode == 1 && texture_faces > 0)
            face_texture = new byte[faces];

        if (texture_faces > 0) {
            triangle_texture_edge_a = new int[texture_faces];
            triangle_texture_edge_b = new int[texture_faces];
            triangle_texture_edge_c = new int[texture_faces];
        }
        nc1.pos = vertexMod_offset;
        nc2.pos = vertexX_offset;
        nc3.pos = vertexY_offset;
        nc4.pos = vertexZ_offset;
        nc5.pos = vSkinBasePos;
        int start_x = 0;
        int start_y = 0;
        int start_z = 0;
        for (int point = 0; point < vertices; point++) {
            int flag = nc1.readUByte();
            int x = 0;
            if ((flag & 1) != 0) {
                x = nc2.readSignedSmart();
            }
            int y = 0;
            if ((flag & 2) != 0) {
                y = nc3.readSignedSmart();

            }
            int z = 0;
            if ((flag & 4) != 0) {
                z = nc4.readSignedSmart();
            }
            vertex_x[point] = start_x + x;
            vertex_y[point] = start_y + y;
            vertex_z[point] = start_z + z;
            start_x = vertex_x[point];
            start_y = vertex_y[point];
            start_z = vertex_z[point];
            if (bone_skin != null)
                bone_skin[point] = nc5.readUByte();

        }
        nc1.pos = color_offset;
        nc2.pos = drawTypeBasePos;
        nc3.pos = facePriorityBasePos;
        nc4.pos = alphaBasePos;
        nc5.pos = tSkinBasePos;
        nc6.pos = textureIdBasePos;
        nc7.pos = textureBasePos;
        for (face = 0; face < faces; face++) {
            face_color[face] = (short) nc1.readUShort();
            if (flags == 1) {
                render_type[face] = nc2.readSignedByte();
            }
            if (priority_opcode == 255) {
                face_render_priorities[face] = nc3.readSignedByte();
            }
            if (alpha_opcode == 1) {
                face_alpha[face] = nc4.readSignedByte();
                if (face_alpha[face] < 0)
                    face_alpha[face] = (256 + face_alpha[face]);

            }
            if (tSkin_opcode == 1)
                muscle_skin[face] = nc5.readUByte();

            if (texture_opcode == 1) {
                face_material[face] = (short) (nc6.readUShort() - 1);
                if (face_material[face] >= 0) {
                    if (render_type != null) {
                        if (render_type[face] < 2 && face_color[face] != 127 && face_color[face] != -27075) {
                            face_material[face] = -1;
                        }
                    }
                }
                if (face_material[face] != -1)
                    face_color[face] = 127;
            }
            if (face_texture != null && face_material[face] != -1) {
                face_texture[face] = (byte) (nc7.readUByte() - 1);
            }
        }
        nc1.pos = faceVPoint_offset;
        nc2.pos = faceMeshLink_offset;
        int coordinate_a = 0;
        int coordinate_b = 0;
        int coordinate_c = 0;
        int last_coordinate = 0;
        for (face = 0; face < faces; face++) {
            int opcode = nc2.readUByte();
            if (opcode == 1) {
                coordinate_a = nc1.readSignedSmart() + last_coordinate;
                last_coordinate = coordinate_a;
                coordinate_b = nc1.readSignedSmart() + last_coordinate;
                last_coordinate = coordinate_b;
                coordinate_c = nc1.readSignedSmart() + last_coordinate;
                last_coordinate = coordinate_c;
                triangle_edge_a[face] = coordinate_a;
                triangle_edge_b[face] = coordinate_b;
                triangle_edge_c[face] = coordinate_c;
            }
            if (opcode == 2) {
                coordinate_b = coordinate_c;
                coordinate_c = nc1.readSignedSmart() + last_coordinate;
                last_coordinate = coordinate_c;
                triangle_edge_a[face] = coordinate_a;
                triangle_edge_b[face] = coordinate_b;
                triangle_edge_c[face] = coordinate_c;
            }
            if (opcode == 3) {
                coordinate_a = coordinate_c;
                coordinate_c = nc1.readSignedSmart() + last_coordinate;
                last_coordinate = coordinate_c;
                triangle_edge_a[face] = coordinate_a;
                triangle_edge_b[face] = coordinate_b;
                triangle_edge_c[face] = coordinate_c;
            }
            if (opcode == 4) {
                int l14 = coordinate_a;
                coordinate_a = coordinate_b;
                coordinate_b = l14;
                coordinate_c = nc1.readSignedSmart() + last_coordinate;
                last_coordinate = coordinate_c;
                triangle_edge_a[face] = coordinate_a;
                triangle_edge_b[face] = coordinate_b;
                triangle_edge_c[face] = coordinate_c;
            }
        }
        nc1.pos = mainBuffer_offset;
        nc2.pos = firstBuffer_offset;
        nc3.pos = secondBuffer_offset;
        nc4.pos = thirdBuffer_offset;
        nc5.pos = fourthBuffer_offset;
        nc6.pos = fifthBuffer_offset;
        for (face = 0; face < texture_faces; face++) {
            int opcode = texture_map[face] & 0xff;
            if (opcode == 0) {
                triangle_texture_edge_a[face] = (short) nc1.readUShort();
                triangle_texture_edge_b[face] = (short) nc1.readUShort();
                triangle_texture_edge_c[face] = (short) nc1.readUShort();
            }
            if (opcode == 1) {
                triangle_texture_edge_a[face] = (short) nc2.readUShort();
                triangle_texture_edge_b[face] = (short) nc2.readUShort();
                triangle_texture_edge_c[face] = (short) nc2.readUShort();
            }
            if (opcode == 2) {
                triangle_texture_edge_a[face] = (short) nc2.readUShort();
                triangle_texture_edge_b[face] = (short) nc2.readUShort();
                triangle_texture_edge_c[face] = (short) nc2.readUShort();
            }
            if (opcode == 3) {
                triangle_texture_edge_a[face] = (short) nc2.readUShort();
                triangle_texture_edge_b[face] = (short) nc2.readUShort();
                triangle_texture_edge_c[face] = (short) nc2.readUShort();
            }
        }
        nc1.pos = pos;
        face = nc1.readUByte();
        repeatTexture = new boolean[faces];
    }

    public void replace(Model model, boolean alpha_flag) {
        vertices = model.vertices;
        faces = model.faces;
        texture_faces = model.texture_faces;
        if (anIntArray1622.length < vertices) {
            anIntArray1622 = new int[vertices + 10000];
            anIntArray1623 = new int[vertices + 10000];
            anIntArray1624 = new int[vertices + 10000];
        }
        vertex_x = anIntArray1622;
        vertex_y = anIntArray1623;
        vertex_z = anIntArray1624;
        for (int point = 0; point < vertices; point++) {
            vertex_x[point] = model.vertex_x[point];
            vertex_y[point] = model.vertex_y[point];
            vertex_z[point] = model.vertex_z[point];
        }
        if (alpha_flag) {
            face_alpha = model.face_alpha;
        } else {
            if (anIntArray1625.length < faces)
                anIntArray1625 = new int[faces + 100];

            face_alpha = anIntArray1625;
            if (model.face_alpha == null) {
                for (int face = 0; face < faces; face++)
                    face_alpha[face] = 0;

            } else {
                for (int face = 0; face < faces; face++)
                    face_alpha[face] = model.face_alpha[face];

            }
        }
        render_type = model.render_type;
        face_color = model.face_color;
        repeatTexture = model.repeatTexture;
        face_render_priorities = model.face_render_priorities;
        face_priority = model.face_priority;
        face_skin = model.face_skin;
        vertex_skin = model.vertex_skin;
        triangle_edge_a = model.triangle_edge_a;
        triangle_edge_b = model.triangle_edge_b;
        triangle_edge_c = model.triangle_edge_c;
        faceHslA = model.faceHslA;
        faceHslB = model.faceHslB;
        faceHslC = model.faceHslC;
        triangle_texture_edge_a = model.triangle_texture_edge_a;
        triangle_texture_edge_b = model.triangle_texture_edge_b;
        triangle_texture_edge_c = model.triangle_texture_edge_c;
        face_texture = model.face_texture;
        texture_map = model.texture_map;
        face_material = model.face_material;
    }

    public void convertNPCTexture(int originalId, int targetId) {
        int assigned = 0;
        this.texture_faces = this.faces;
        triangle_texture_edge_a = new int[faces];
        triangle_texture_edge_b = new int[faces];
        triangle_texture_edge_c = new int[faces];
        for(int i = 0; i < this.faces; ++i) { // loops through all the triangle faces
            if (this.face_color[i] == originalId) {
                this.face_color[i] = targetId; // sets triangleColours[i] to targetId
                this.triangle_texture_edge_a[assigned] = this.triangle_edge_a[i]; // pretty much updates
                this.triangle_texture_edge_b[assigned] = this.triangle_edge_b[i];
                this.triangle_texture_edge_c[assigned] = this.triangle_edge_c[i];
                assigned++;
            }
        }
    }

    private final int method465(Model model, int face) {
        int vertex = -1;
        int x = model.vertex_x[face];
        int y = model.vertex_y[face];
        int z = model.vertex_z[face];
        for (int index = 0; index < vertices; index++) {
            if (x != vertex_x[index] || y != vertex_y[index] || z != vertex_z[index])
                continue;
            vertex = index;
            break;
        }
        if (vertex == -1) {
            vertex_x[vertices] = x;
            vertex_y[vertices] = y;
            vertex_z[vertices] = z;
            if (model.bone_skin != null)
                bone_skin[vertices] = model.bone_skin[face];

            vertex = vertices++;
        }
        return vertex;
    }

    public void calc_diagonals() {
        super.model_height = 0;
        diagonal_2D = 0;
        max_y = 0;
        for (int i = 0; i < vertices; i++) {
            int j = vertex_x[i];
            int k = vertex_y[i];
            int l = vertex_z[i];
            if (-k > super.model_height)
                super.model_height = -k;
            if (k > max_y)
                max_y = k;
            int i1 = j * j + l * l;
            if (i1 > diagonal_2D)
                diagonal_2D = i1;
        }
        diagonal_2D = (int) (Math.sqrt(diagonal_2D) + 0.98999999999999999D);
        diagonal_3D = (int) (Math.sqrt(diagonal_2D * diagonal_2D + super.model_height
            * super.model_height) + 0.98999999999999999D);
        scene_depth = diagonal_3D
            + (int) (Math.sqrt(diagonal_2D * diagonal_2D + max_y
            * max_y) + 0.98999999999999999D);
    }

    public void computeSphericalBounds() {
        super.model_height = 0;
        max_y = 0;
        for (int i = 0; i < vertices; i++) {
            int j = vertex_y[i];
            if (-j > super.model_height)
                super.model_height = -j;
            if (j > max_y)
                max_y = j;
        }

        diagonal_3D = (int) (Math.sqrt(diagonal_2D * diagonal_2D + super.model_height
            * super.model_height) + 0.98999999999999999D);
        scene_depth = diagonal_3D
            + (int) (Math.sqrt(diagonal_2D * diagonal_2D + max_y
            * max_y) + 0.98999999999999999D);
    }

    public void calculateVertexData(int i) {
        super.model_height = 0;
        diagonal_2D = 0;
        max_y = 0;
        min_x = 0xf423f;
        max_x = 0xfff0bdc1;
        max_z = 0xfffe7961;
        min_z = 0x1869f;
        for (int j = 0; j < vertices; j++) {
            int k = vertex_x[j];
            int l = vertex_y[j];
            int i1 = vertex_z[j];
            if (k < min_x)
                min_x = k;
            if (k > max_x)
                max_x = k;
            if (i1 < min_z)
                min_z = i1;
            if (i1 > max_z)
                max_z = i1;
            if (-l > super.model_height)
                super.model_height = -l;
            if (l > max_y)
                max_y = l;
            int j1 = k * k + i1 * i1;
            if (j1 > diagonal_2D)
                diagonal_2D = j1;
        }

        diagonal_2D = (int) Math.sqrt(diagonal_2D);
        diagonal_3D = (int) Math.sqrt(diagonal_2D * diagonal_2D + super.model_height * super.model_height);
        if (i != 21073) {
            return;
        } else {
            scene_depth = diagonal_3D + (int) Math.sqrt(diagonal_2D * diagonal_2D + max_y * max_y);
            return;
        }
    }

    public void scale2(int i) {
        for (int i1 = 0; i1 < vertices; i1++) {
            vertex_x[i1] = vertex_x[i1] / i;
            vertex_y[i1] = vertex_y[i1] / i;
            vertex_z[i1] = vertex_z[i1] / i;
        }
    }

    public void skin() {
        if (bone_skin != null) {
            int ai[] = new int[256];
            int j = 0;
            for (int l = 0; l < vertices; l++) {
                int j1 = bone_skin[l];
                ai[j1]++;
                if (j1 > j)
                    j = j1;
            }
            vertex_skin = new int[j + 1][];
            for (int k1 = 0; k1 <= j; k1++) {
                vertex_skin[k1] = new int[ai[k1]];
                ai[k1] = 0;
            }
            for (int j2 = 0; j2 < vertices; j2++) {
                int l2 = bone_skin[j2];
                vertex_skin[l2][ai[l2]++] = j2;
            }
            bone_skin = null;
        }
        if (muscle_skin != null) {
            int ai1[] = new int[256];
            int k = 0;
            for (int i1 = 0; i1 < faces; i1++) {
                int l1 = muscle_skin[i1];
                ai1[l1]++;
                if (l1 > k)
                    k = l1;
            }
            face_skin = new int[k + 1][];
            for (int uid = 0; uid <= k; uid++) {
                face_skin[uid] = new int[ai1[uid]];
                ai1[uid] = 0;
            }
            for (int k2 = 0; k2 < faces; k2++) {
                int i3 = muscle_skin[k2];
                face_skin[i3][ai1[i3]++] = k2;
            }
            muscle_skin = null;
        }
    }

    private void transform(int opcode, int skin[], int x, int y, int z) {
        int length = skin.length;
        if (opcode == 0) {
            int offset = 0;
            xAnimOffset = 0;
            yAnimOffset = 0;
            zAnimOffset = 0;
            for (int skin_index = 0; skin_index < length; skin_index++) {
                int id = skin[skin_index];
                if (id < vertex_skin.length) {
                    int vertex[] = vertex_skin[id];
                    for (int index = 0; index < vertex.length; index++) {
                        int tri = vertex[index];
                        xAnimOffset += vertex_x[tri];
                        yAnimOffset += vertex_y[tri];
                        zAnimOffset += vertex_z[tri];
                        offset++;
                    }
                }
            }
            if (offset > 0) {
                xAnimOffset = xAnimOffset / offset + x;
                yAnimOffset = yAnimOffset / offset + y;
                zAnimOffset = zAnimOffset / offset + z;
                return;
            } else {
                xAnimOffset = x;
                yAnimOffset = y;
                zAnimOffset = z;
                return;
            }
        }
        if (opcode == 1) {
            for (int skin_index = 0; skin_index < length; skin_index++) {
                int id = skin[skin_index];
                if (id < vertex_skin.length) {
                    int vertex[] = vertex_skin[id];
                    for (int index = 0; index < vertex.length; index++) {
                        int tri = vertex[index];
                        vertex_x[tri] += x;
                        vertex_y[tri] += y;
                        vertex_z[tri] += z;
                    }
                }
            }
            return;
        }
        if (opcode == 2) {//rotation
            for (int skin_index = 0; skin_index < length; skin_index++) {
                int id = skin[skin_index];
                if (id < vertex_skin.length) {
                    int vertex[] = vertex_skin[id];
                    for (int index = 0; index < vertex.length; index++) {
                        int tri = vertex[index];
                        vertex_x[tri] -= xAnimOffset;
                        vertex_y[tri] -= yAnimOffset;
                        vertex_z[tri] -= zAnimOffset;
                        //int k6 = (x & 0xff) * 8;
                        //int l6 = (y & 0xff) * 8;
                        //int i7 = (z & 0xff) * 8;
                        if (z != 0) {//if (i7 != 0) {
                            int rot_x = SINE[z];//i7
                            int rot_y = COSINE[z];//i7
                            int rot_z = vertex_y[tri] * rot_x + vertex_x[tri] * rot_y >> 16;
                            vertex_y[tri] = vertex_y[tri] * rot_y - vertex_x[tri] * rot_x >> 16;
                            vertex_x[tri] = rot_z;
                        }
                        if (x != 0) {//if (k6 != 0) {
                            int rot_x = SINE[x];//k6
                            int rot_y = COSINE[x];//k6
                            int rot_z = vertex_y[tri] * rot_y - vertex_z[tri] * rot_x >> 16;
                            vertex_z[tri] = vertex_y[tri] * rot_x + vertex_z[tri] * rot_y >> 16;
                            vertex_y[tri] = rot_z;
                        }
                        if (y != 0) {//if (l6 != 0) {
                            int rot_x = SINE[y];//l6
                            int rot_y = COSINE[y];//l6
                            int rot_z = vertex_z[tri] * rot_x + vertex_x[tri] * rot_y >> 16;
                            vertex_z[tri] = vertex_z[tri] * rot_y - vertex_x[tri] * rot_x >> 16;
                            vertex_x[tri] = rot_z;
                        }
                        vertex_x[tri] += xAnimOffset;
                        vertex_y[tri] += yAnimOffset;
                        vertex_z[tri] += zAnimOffset;
                    }

                }
            }
            return;
        }
        if (opcode == 3) {
            for (int skin_index = 0; skin_index < length; skin_index++) {
                int id = skin[skin_index];
                if (id < vertex_skin.length) {
                    int vertex[] = vertex_skin[id];
                    for (int index = 0; index < vertex.length; index++) {
                        int tri = vertex[index];
                        vertex_x[tri] -= xAnimOffset;
                        vertex_y[tri] -= yAnimOffset;
                        vertex_z[tri] -= zAnimOffset;
                        vertex_x[tri] = (vertex_x[tri] * x) / 128;
                        vertex_y[tri] = (vertex_y[tri] * y) / 128;
                        vertex_z[tri] = (vertex_z[tri] * z) / 128;
                        vertex_x[tri] += xAnimOffset;
                        vertex_y[tri] += yAnimOffset;
                        vertex_z[tri] += zAnimOffset;
                    }
                }
            }
            return;
        }
        if (opcode == 5 && face_skin != null && face_alpha != null) {
            for (int skin_index = 0; skin_index < length; skin_index++) {
                int id = skin[skin_index];
                if (id < face_skin.length) {
                    int face[] = face_skin[id];
                    for (int index = 0; index < face.length; index++) {
                        int tri = face[index];

                        face_alpha[tri] += x * 8;
                        if (face_alpha[tri] < 0)
                            face_alpha[tri] = 0;

                        if (face_alpha[tri] > 255)
                            face_alpha[tri] = 255;

                    }
                }
            }
        }
    }


    private void transformSkin(int animationType, int skin[], int x, int y, int z) {

        int i1 = skin.length;
        if (animationType == 0) {
            int j1 = 0;
            xAnimOffset = 0;
            yAnimOffset = 0;
            zAnimOffset = 0;
            for (int k2 = 0; k2 < i1; k2++) {
                int l3 = skin[k2];
                if (l3 < vertex_skin.length) {
                    int ai5[] = vertex_skin[l3];
                    for (int i5 = 0; i5 < ai5.length; i5++) {
                        int j6 = ai5[i5];
                        xAnimOffset += vertex_x[j6];
                        yAnimOffset += vertex_y[j6];
                        zAnimOffset += vertex_z[j6];
                        j1++;
                    }

                }
            }

            if (j1 > 0) {
                xAnimOffset = (int) (xAnimOffset / j1 + x);
                yAnimOffset = (int) (yAnimOffset / j1 + y);
                zAnimOffset = (int) (zAnimOffset / j1 + z);
                return;
            } else {
                xAnimOffset = (int) x;
                yAnimOffset = (int) y;
                zAnimOffset = (int) z;
                return;
            }
        }
        if (animationType == 1) {
            for (int k1 = 0; k1 < i1; k1++) {
                int l2 = skin[k1];
                if (l2 < vertex_skin.length) {
                    int ai1[] = vertex_skin[l2];
                    for (int i4 = 0; i4 < ai1.length; i4++) {
                        int j5 = ai1[i4];
                        vertex_x[j5] += x;
                        vertex_y[j5] += y;
                        vertex_z[j5] += z;
                    }

                }
            }

            return;
        }
        if (animationType == 2) {
            for (int l1 = 0; l1 < i1; l1++) {
                int i3 = skin[l1];
                if (i3 < vertex_skin.length) {
                    int auid[] = vertex_skin[i3];
                    for (int j4 = 0; j4 < auid.length; j4++) {
                        int k5 = auid[j4];
                        vertex_x[k5] -= xAnimOffset;
                        vertex_y[k5] -= yAnimOffset;
                        vertex_z[k5] -= zAnimOffset;
                        int k6 = (x & 0xff) * 8;
                        int l6 = (y & 0xff) * 8;
                        int i7 = (z & 0xff) * 8;
                        if (i7 != 0) {
                            int j7 = SINE[i7];
                            int i8 = COSINE[i7];
                            int l8 = vertex_y[k5] * j7 + vertex_x[k5] * i8 >> 16;
                            vertex_y[k5] = vertex_y[k5] * i8 - vertex_x[k5] * j7 >> 16;
                            vertex_x[k5] = l8;
                        }
                        if (k6 != 0) {
                            int k7 = SINE[k6];
                            int j8 = COSINE[k6];
                            int i9 = vertex_y[k5] * j8 - vertex_z[k5] * k7 >> 16;
                            vertex_z[k5] = vertex_y[k5] * k7 + vertex_z[k5] * j8 >> 16;
                            vertex_y[k5] = i9;
                        }
                        if (l6 != 0) {
                            int l7 = SINE[l6];
                            int k8 = COSINE[l6];
                            int j9 = vertex_z[k5] * l7 + vertex_x[k5] * k8 >> 16;
                            vertex_z[k5] = vertex_z[k5] * k8 - vertex_x[k5] * l7 >> 16;
                            vertex_x[k5] = j9;
                        }
                        vertex_x[k5] += xAnimOffset;
                        vertex_y[k5] += yAnimOffset;
                        vertex_z[k5] += zAnimOffset;
                    }

                }
            }

            return;
        }
        if (animationType == 3) {
            for (int uid = 0; uid < i1; uid++) {
                int j3 = skin[uid];
                if (j3 < vertex_skin.length) {
                    int ai3[] = vertex_skin[j3];
                    for (int k4 = 0; k4 < ai3.length; k4++) {
                        int l5 = ai3[k4];
                        vertex_x[l5] -= xAnimOffset;
                        vertex_y[l5] -= yAnimOffset;
                        vertex_z[l5] -= zAnimOffset;
                        vertex_x[l5] = (int) ((vertex_x[l5] * x) / 128);
                        vertex_y[l5] = (int) ((vertex_y[l5] * y) / 128);
                        vertex_z[l5] = (int) ((vertex_z[l5] * z) / 128);
                        vertex_x[l5] += xAnimOffset;
                        vertex_y[l5] += yAnimOffset;
                        vertex_z[l5] += zAnimOffset;
                    }

                }
            }

            return;
        }
        if (animationType == 5 && face_skin != null && face_alpha != null) {
            for (int j2 = 0; j2 < i1; j2++) {
                int k3 = skin[j2];
                if (k3 < face_skin.length) {
                    int ai4[] = face_skin[k3];
                    for (int l4 = 0; l4 < ai4.length; l4++) {
                        int i6 = ai4[l4];
                        face_alpha[i6] += x * 8;
                        if (face_alpha[i6] < 0)
                            face_alpha[i6] = 0;
                        if (face_alpha[i6] > 255)
                            face_alpha[i6] = 255;
                    }

                }
            }

        }
    }

    public void interpolate(int frameId) {
        if (vertex_skin == null)
            return;

        if (frameId == -1)
            return;

        Animation frame = Animation.get(frameId);
        if (frame == null)
            return;

        Skins skin = frame.skins;
        xAnimOffset = 0;
        yAnimOffset = 0;
        zAnimOffset = 0;

        for (int index = 0; index < frame.frames; index++) {
            int pos = frame.translation_modifier[index];
            //Change skin.cache[pos] to skin.cache[2] for funny animations
            transform(skin.opcodes[pos], skin.cache[pos], frame.x_modifier[index], frame.y_modifier[index], frame.z_modifier[index]);

        }

    }


    public void mix(int label[], int idle, int current) {
        if (current == -1)
            return;

        if (label == null || idle == -1) {
            interpolate(current);
            return;
        }
        Animation anim = Animation.get(current);
        if (anim == null)
            return;

        Animation skin = Animation.get(idle);
        if (skin == null) {
            interpolate(current);
            return;
        }
        Skins list = anim.skins;
        xAnimOffset = 0;
        yAnimOffset = 0;
        zAnimOffset = 0;
        int id = 0;
        int table = label[id++];
        for (int index = 0; index < anim.frames; index++) {
            int condition;
            for (condition = anim.translation_modifier[index]; condition > table; table = label[id++])
                ;//empty
            if (condition != table || list.opcodes[condition] == 0)
                transform(list.opcodes[condition], list.cache[condition], anim.x_modifier[index], anim.y_modifier[index], anim.z_modifier[index]);
        }
        xAnimOffset = 0;
        yAnimOffset = 0;
        zAnimOffset = 0;
        id = 0;
        table = label[id++];
        for (int index = 0; index < skin.frames; index++) {
            int condition;
            for (condition = skin.translation_modifier[index]; condition > table; table = label[id++])
                ;//empty
            if (condition == table || list.opcodes[condition] == 0)
                transform(list.opcodes[condition], list.cache[condition], skin.x_modifier[index], skin.y_modifier[index], skin.z_modifier[index]);

        }
    }


    public void rotate_90() {
        for (int point = 0; point < vertices; point++) {
            int k = vertex_x[point];
            vertex_x[point] = vertex_z[point];
            vertex_z[point] = -k;
        }
    }

    public void leanOverX(int i) {
        int k = SINE[i];
        int l = COSINE[i];
        for (int point = 0; point < vertices; point++) {
            int j1 = vertex_y[point] * l - vertex_z[point] * k >> 16;
            vertex_z[point] = vertex_y[point] * k + vertex_z[point] * l >> 16;
            vertex_y[point] = j1;
        }
    }

    public void translate(int x, int y, int z) {
        for (int point = 0; point < vertices; point++) {
            vertex_x[point] += x;
            vertex_y[point] += y;
            vertex_z[point] += z;
        }
    }

    public void recolor(int found, int replace) {
        if (face_color != null)
            for (int face = 0; face < faces; face++)
                if (face_color[face] == (short) found)
                    face_color[face] = (short) replace;
    }

    public void retexture(short found, short replace) {
        if(face_material != null) {
            for (int face = 0; face < faces; face++) {
                if (face_material[face] == found) {
                    face_material[face] = replace;
                }
            }
        }
    }

    public void color_to_texture(Model model, short src, short dst, boolean debug) {
        if(model.face_color != null) {
            if(model != null) {
                //if(debug)
                //    System.out.println("model * " + Arrays.toString(model.color));//use to find the color you want to replace


                if(model.render_type == null) {
                    model.render_type = new int[model.faces];
                    for(int face = 0; face < faces; face++) {
                        if(model.face_color[face] == src)
                            model.render_type[face] = 2;
                        else
                            model.render_type[face] = 0;

                    }
                } else {
                    for(int face = 0; face < faces; face++) {
                        if(model.face_color[face] == src)
                            model.render_type[face] = 2;
                        else
                            model.render_type[face] = 0;
                    }
                }
                render_type = model.render_type;

                if(model.face_material == null) {
                    model.face_material = new int[model.faces];
                    for(int face = 0; face < faces; face++) {
                        if(model.face_color[face] == src)
                            model.face_material[face] = dst;

                    }
                } else {
                    for(int face = 0; face < faces; face++) {
                        if(model.face_color[face] == src)
                            model.face_material[face] = dst;

                    }
                }
                face_material = model.face_material;


                if(model.face_texture == null) {
                    model.face_texture = new byte[model.faces];
                }

                texture_map = new byte[model.faces];
                triangle_texture_edge_a = new int[model.faces];
                triangle_texture_edge_b = new int[model.faces];
                triangle_texture_edge_c = new int[model.faces];
                for(int face = 0; face < model.faces; face++) {
                    if(model.face_color[face] == src) {
                        model.face_texture[face] = (byte) 2;
                        model.face_color[face] = 127;
                        model.texture_map[face] = 1;
                        model.texture_faces++;
                    }
                    if(model.face_color[face] != 127 && src == -1) {
                        render_type[face] = 0;
                        face_material[face] = -1;
                    }
                }
                for(int face = 0; face < model.texture_faces; face++) {
                    model.triangle_texture_edge_a[face] = (short) model.triangle_edge_a[face];
                    model.triangle_texture_edge_b[face] = (short) model.triangle_edge_b[face];
                    model.triangle_texture_edge_c[face] = (short) model.triangle_edge_c[face];
                }
            }
        }
    }

    public void invert() {
        for (int index = 0; index < vertices; index++)
            vertex_z[index] = -vertex_z[index];

        for (int face = 0; face < faces; face++) {
            int l = triangle_edge_a[face];
            triangle_edge_a[face] = triangle_edge_c[face];
            triangle_edge_c[face] = l;
        }
    }

    public void scale(int i, int j, int l) {
        for (int index = 0; index < vertices; index++) {
            vertex_x[index] = (vertex_x[index] * i) / 128;
            vertex_y[index] = (vertex_y[index] * l) / 128;
            vertex_z[index] = (vertex_z[index] * j) / 128;
        }
    }

    public void light(int i, int j, int k, int l, int i1, boolean flag) {
        light(i, j, k, l, i1, flag, false);
    }

    public void light(int i, int j, int k, int l, int i1, boolean flag, boolean player) {
        int j1 = (int) Math.sqrt(k * k + l * l + i1 * i1);
        int k1 = j * j1 >> 8;
        faceHslA = new int[faces];
        faceHslB = new int[faces];
        faceHslC = new int[faces];
        if (super.normals == null) {
            super.normals = new Vertex[vertices];
            for (int index = 0; index < vertices; index++)
                super.normals[index] = new Vertex();

        }
        for (int face = 0; face < faces; face++) {
            int j2 = triangle_edge_a[face];
            int l2 = triangle_edge_b[face];
            int i3 = triangle_edge_c[face];
            int j3 = vertex_x[l2] - vertex_x[j2];
            int k3 = vertex_y[l2] - vertex_y[j2];
            int l3 = vertex_z[l2] - vertex_z[j2];
            int i4 = vertex_x[i3] - vertex_x[j2];
            int j4 = vertex_y[i3] - vertex_y[j2];
            int k4 = vertex_z[i3] - vertex_z[j2];
            int l4 = k3 * k4 - j4 * l3;
            int i5 = l3 * i4 - k4 * j3;
            int j5;
            for (j5 = j3 * j4 - i4 * k3; l4 > 8192 || i5 > 8192 || j5 > 8192 || l4 < -8192 || i5 < -8192 || j5 < -8192; j5 >>= 1) {
                l4 >>= 1;
                i5 >>= 1;
            }
            int k5 = (int) Math.sqrt(l4 * l4 + i5 * i5 + j5 * j5);
            if (k5 <= 0)
                k5 = 1;

            l4 = (l4 * 256) / k5;
            i5 = (i5 * 256) / k5;
            j5 = (j5 * 256) / k5;

            int texture_id;
            int type;
            if (render_type != null)
                type = render_type[face];
            else
                type = 0;

            if (face_material == null) {
                texture_id = -1;
            } else {
                texture_id = face_material[face];
            }

            if (render_type == null || (render_type[face] & 1) == 0) {
                Vertex class33_2 = super.normals[j2];
                class33_2.x += l4;
                class33_2.y += i5;
                class33_2.z += j5;
                class33_2.magnitude++;
                class33_2 = super.normals[l2];
                class33_2.x += l4;
                class33_2.y += i5;
                class33_2.z += j5;
                class33_2.magnitude++;
                class33_2 = super.normals[i3];
                class33_2.x += l4;
                class33_2.y += i5;
                class33_2.z += j5;
                class33_2.magnitude++;
            } else {
                if (texture_id != -1) {
                    type = 2;
                }
                int light = i + (k * l4 + l * i5 + i1 * j5) / (k1 + k1 / 2);
                faceHslA[face] = method481(face_color[face], light, type);
            }
        }
        if (flag) {
            method480(i, k1, k, l, i1, player);
            calc_diagonals();//method466
        } else {
            gouraud_vertex = new Vertex[vertices];
            for (int point = 0; point < vertices; point++) {
                Vertex class33 = super.normals[point];
                Vertex class33_1 = gouraud_vertex[point] = new Vertex();
                class33_1.x = class33.x;
                class33_1.y = class33.y;
                class33_1.z = class33.z;
                class33_1.magnitude = class33.magnitude;
            }
            calculateVertexData(21073);
        }
    }

    public final void doShading(int i, int j, int k, int l, int i1) {
        method480(i, j, k, l, i1, false);
    }

    public final void method480(int i, int j, int k, int l, int i1, boolean player) {
        for (int j1 = 0; j1 < faces; j1++) {
            int k1 = triangle_edge_a[j1];
            int i2 = triangle_edge_b[j1];
            int j2 = triangle_edge_c[j1];
            int texture_id;
            if(face_material == null) {
                texture_id = -1;
            } else {
                texture_id = face_material[j1];
                if (player) {
                    if(face_alpha != null && face_color != null) {
                        if(face_color[j1] == 0 && face_render_priorities[j1] == 0) {
                            if(render_type[j1] == 2 && face_material[j1] == -1) {
                                face_alpha[j1] = 255;
                            }
                        }
                    } else if(face_alpha == null) {
                        if(face_color[j1] == 0 && face_render_priorities[j1] == 0) {
                            if(face_material[j1] == -1) {
                                face_alpha = new int[faces];
                                if(render_type[j1] == 2) {
                                    face_alpha[j1] = 255;
                                }
                            }
                        }
                    }
                }
            }

            if (render_type == null) {
                int type;
                if(texture_id != -1) {
                    type = 2;
                } else {
                    type = 1;
                }
                int hsl = face_color[j1] & 0xffff;
                Vertex vertex = super.normals[k1];
                int light = i + (k * vertex.x + l * vertex.y + i1 * vertex.z) / (j * vertex.magnitude);
                faceHslA[j1] = method481(hsl, light, type);
                vertex = super.normals[i2];
                light = i + (k * vertex.x + l * vertex.y + i1 * vertex.z) / (j * vertex.magnitude);
                faceHslB[j1] = method481(hsl, light, type);
                vertex = super.normals[j2];
                light = i + (k * vertex.x + l * vertex.y + i1 * vertex.z) / (j * vertex.magnitude);
                faceHslC[j1] = method481(hsl, light, type);
            } else if ((render_type[j1] & 1) == 0) {
                int type = render_type[j1];
                if(texture_id != -1) {
                    type = 2;
                }
                int hsl = face_color[j1] & 0xffff;
                Vertex vertex = super.normals[k1];
                int light = i + (k * vertex.x + l * vertex.y + i1 * vertex.z) / (j * vertex.magnitude);
                faceHslA[j1] = method481(hsl, light, type);
                vertex = super.normals[i2];
                light = i + (k * vertex.x + l * vertex.y + i1 * vertex.z) / (j * vertex.magnitude);
                faceHslB[j1] = method481(hsl, light, type);
                vertex = super.normals[j2];
                light = i + (k * vertex.x + l * vertex.y + i1 * vertex.z) / (j * vertex.magnitude);
                faceHslC[j1] = method481(hsl, light, type);
            }
        }

        super.normals = null;
        gouraud_vertex = null;
        bone_skin = null;
        muscle_skin = null;
        face_color = null;
    }

    public static final int method481(int i, int j, int k) {
        if (i == 65535)
            return 0;

        if ((k & 2) == 2) {
            if (j < 0)
                j = 0;
            else if (j > 127)
                j = 127;

            j = 127 - j;
            return j;
        }

        j = j * (i & 0x7f) >> 7;
        if (j < 2)
            j = 2;
        else if (j > 126)
            j = 126;

        return (i & 0xff80) + j;
    }

    //inventory / widget model rendering (render_2D)
    public final void render_2D(int roll, int yaw, int pitch, int start_x, int start_y, int zoom) {
        int depth = 0;
        int center_x = Rasterizer3D.center_x;
        int center_y = Rasterizer3D.center_y;
        int depth_sin = SINE[depth];
        int depth_cos = COSINE[depth];
        int roll_sin = SINE[roll];
        int roll_cos = COSINE[roll];
        int yaw_sin = SINE[yaw];
        int yaw_cos = COSINE[yaw];
        int pitch_sin = SINE[pitch];
        int pitch_cos = COSINE[pitch];
        int position = start_y * pitch_sin + zoom * pitch_cos >> 16;
        for (int index = 0; index < vertices; index++) {
            int x = vertex_x[index];
            int y = vertex_y[index];
            int z = vertex_z[index];
            if (yaw != 0) {
                int rotated_x = y * yaw_sin + x * yaw_cos >> 16;
                y = y * yaw_cos - x * yaw_sin >> 16;
                x = rotated_x;
            }
            if (depth != 0) {
                int rotated_y = y * depth_cos - z * depth_sin >> 16;
                z = y * depth_sin + z * depth_cos >> 16;
                y = rotated_y;
            }
            if (roll != 0) {
                int rotated_z = z * roll_sin + x * roll_cos >> 16;
                z = z * roll_cos - x * roll_sin >> 16;
                x = rotated_z;
            }
            x += start_x;
            y += start_y;
            z += zoom;

            int y_offset = y * pitch_cos - z * pitch_sin >> 16;
            z = y * pitch_sin + z * pitch_cos >> 16;
            y = y_offset;

            projected_vertex_z[index] = z - position;
            projected_vertex_x[index] = center_x + (x << 9) / z;
            projected_vertex_y[index] = center_y + (y << 9) / z;
            if (texture_faces > 0) {
                camera_vertex_x[index] = x;
                camera_vertex_y[index] = y;
                camera_vertex_z[index] = z;
            }

        }
        try {
            method483(false, false,  0);
        } catch (Exception _ex) {
            _ex.printStackTrace();
            System.out.println("Could not rotate and project item!");
        }
    }
    public static final int VIEW_DISTANCE = 3500; //3500 or 4500, 3500 provides better performance.

    @Override
    public final void render_3D(int orientation, int cos_y, int sin_y, int sin_x, int cos_x, int start_x, int start_y, int depth, long uid) {

        int scene_x = depth * cos_x - start_x * sin_x >> 16;
        int scene_y = start_y * cos_y + scene_x * sin_y >> 16;
        int dimension_sin_y = diagonal_2D * sin_y >> 16;
        int pos = scene_y + dimension_sin_y;
        if (pos <= 50 || scene_y >= VIEW_DISTANCE)
            return;

        int x_rot = depth * sin_x + start_x * cos_x >> 16;
        int obj_x = x_rot - diagonal_2D << SceneGraph.view_dist;
        if (obj_x / pos >= Rasterizer2D.viewport_center_y)
            return;

        int obj_width = x_rot + diagonal_2D << SceneGraph.view_dist;
        if (obj_width / pos <= -Rasterizer2D.viewport_center_y)
            return;

        int y_rot = start_y * sin_y - scene_x * cos_y >> 16;
        int dimension_cos_y = diagonal_2D * cos_y >> 16;
        int obj_height = y_rot + dimension_cos_y << SceneGraph.view_dist;
        if (obj_height / pos <= -Rasterizer2D.viewport_center_x)
            return;


        int offset = dimension_cos_y + (super.model_height * sin_y >> 16);
        int obj_y = y_rot - offset << SceneGraph.view_dist;
        if (obj_y / pos >= Rasterizer2D.viewport_center_x)
            return;


        int size = dimension_sin_y + (super.model_height * cos_y >> 16);
        boolean flag = false;
        if (scene_y - size <= 50)
            flag = true;

        boolean flag1 = false;
        if (uid > 0 && obj_exists) {
            int obj_height_offset = scene_y - offset;
            if (obj_height_offset <= 50)
                obj_height_offset = 50;
            if (x_rot > 0) {
                obj_x /= pos;
                obj_width /= obj_height_offset;
            } else {
                obj_width /= pos;
                obj_x /= obj_height_offset;
            }
            if (y_rot > 0) {
                obj_y /= pos;
                obj_height /= obj_height_offset;
            } else {
                obj_height /= pos;
                obj_y /= obj_height_offset;
            }
            int mouse_x = anInt1685 - Rasterizer3D.center_x;
            int mouse_y = anInt1686 - Rasterizer3D.center_y;
            if (mouse_x > obj_x && mouse_x < obj_width && mouse_y > obj_y && mouse_y < obj_height)
                if (within_tile)
                    anIntArray1688[anInt1687++] = uid;
                else
                    flag1 = true;
        }
        int center_x = Rasterizer3D.center_x;
        int center_y = Rasterizer3D.center_y;
        int sine_x = 0;
        int cosine_x = 0;
        if (orientation != 0) {
            sine_x = SINE[orientation];
            cosine_x = COSINE[orientation];
        }
        for (int index = 0; index < vertices; index++) {
            int raster_x = vertex_x[index];
            int raster_y = vertex_y[index];
            int raster_z = vertex_z[index];

            if (orientation != 0) {
                int rotated_x = raster_z * sine_x + raster_x * cosine_x >> 16;
                raster_z = raster_z * cosine_x - raster_x * sine_x >> 16;
                raster_x = rotated_x;

            }
            raster_x += start_x;
            raster_y += start_y;
            raster_z += depth;

            int position = raster_z * sin_x + raster_x * cos_x >> 16;
            raster_z = raster_z * cos_x - raster_x * sin_x >> 16;
            raster_x = position;

            position = raster_y * sin_y - raster_z * cos_y >> 16;
            raster_z = raster_y * cos_y + raster_z * sin_y >> 16;
            raster_y = position;


            projected_vertex_z[index] = raster_z - scene_y;
            if (raster_z >= 50) {
                projected_vertex_x[index] = center_x + (raster_x << SceneGraph.view_dist) / raster_z;
                projected_vertex_y[index] = center_y + (raster_y << SceneGraph.view_dist) / raster_z;
            } else {
                projected_vertex_x[index] = -5000;
                flag = true;
            }
            if (flag || texture_faces > 0) {
                camera_vertex_x[index] = raster_x;
                camera_vertex_y[index] = raster_y;
                camera_vertex_z[index] = raster_z;
            }
        }
        try {
            method483(flag, flag1,  uid);
            return;
        } catch (Exception _ex) {
            return;
        }
    }

    private void method483(boolean flag, boolean flag1, long uid) {

        for (int j = 0; j < scene_depth; j++)
            depthListIndices[j] = 0;

        for (int face = 0; face < faces; face++) {
            if (render_type == null || render_type[face] != -1) {
                int a = triangle_edge_a[face];
                int b = triangle_edge_b[face];
                int c = triangle_edge_c[face];
                int x_a = projected_vertex_x[a];
                int x_b = projected_vertex_x[b];
                int x_c = projected_vertex_x[c];
                if (flag && (x_a == -5000 || x_b == -5000 || x_c == -5000)) {
                    outOfReach[face] = true;
                    int j5 = (projected_vertex_z[a] + projected_vertex_z[b] + projected_vertex_z[c]) / 3 + diagonal_3D;
                    faceLists[j5][depthListIndices[j5]++] = face;
                } else {
                    if (flag1 && entered_clickbox(anInt1685, anInt1686, projected_vertex_y[a], projected_vertex_y[b], projected_vertex_y[c], x_a, x_b, x_c)) {
                        anIntArray1688[anInt1687++] = uid;
                        flag1 = false;
                    }
                    if ((x_a - x_b) * (projected_vertex_y[c] - projected_vertex_y[b]) - (projected_vertex_y[a] - projected_vertex_y[b]) * (x_c - x_b) > 0) {
                        outOfReach[face] = false;
                        if (x_a < 0 || x_b < 0 || x_c < 0 || x_a > Rasterizer2D.center_x || x_b > Rasterizer2D.center_x || x_c > Rasterizer2D.center_x)
                            hasAnEdgeToRestrict[face] = true;
                        else
                            hasAnEdgeToRestrict[face] = false;

                        int k5 = (projected_vertex_z[a] + projected_vertex_z[b] + projected_vertex_z[c]) / 3 + diagonal_3D;
                        faceLists[k5][depthListIndices[k5]++] = face;
                    }
                }
            }
        }
        if (face_render_priorities == null) {
            for (int i1 = scene_depth - 1; i1 >= 0; i1--) {
                int l1 = depthListIndices[i1];
                if (l1 > 0) {
                    int ai[] = faceLists[i1];
                    for (int j3 = 0; j3 < l1; j3++)
                        rasterize(ai[j3]);

                }
            }
            return;
        }
        for (int j1 = 0; j1 < 12; j1++) {
            anIntArray1673[j1] = 0;
            anIntArray1677[j1] = 0;
        }
        for (int i2 = scene_depth - 1; i2 >= 0; i2--) {
            int k2 = depthListIndices[i2];
            if (k2 > 0) {
                int ai1[] = faceLists[i2];
                for (int i4 = 0; i4 < k2; i4++) {
                    int l4 = ai1[i4];
                    byte l5 = face_render_priorities[l4];
                    int j6 = anIntArray1673[l5]++;
                    anIntArrayArray1674[l5][j6] = l4;
                    if (l5 < 10)
                        anIntArray1677[l5] += i2;
                    else if (l5 == 10)
                        anIntArray1675[j6] = i2;
                    else
                        anIntArray1676[j6] = i2;
                }

            }
        }

        int l2 = 0;
        if (anIntArray1673[1] > 0 || anIntArray1673[2] > 0)
            l2 = (anIntArray1677[1] + anIntArray1677[2]) / (anIntArray1673[1] + anIntArray1673[2]);
        int k3 = 0;
        if (anIntArray1673[3] > 0 || anIntArray1673[4] > 0)
            k3 = (anIntArray1677[3] + anIntArray1677[4]) / (anIntArray1673[3] + anIntArray1673[4]);
        int j4 = 0;
        if (anIntArray1673[6] > 0 || anIntArray1673[8] > 0)
            j4 = (anIntArray1677[6] + anIntArray1677[8]) / (anIntArray1673[6] + anIntArray1673[8]);

        int i6 = 0;
        int k6 = anIntArray1673[10];
        int auid[] = anIntArrayArray1674[10];
        int ai3[] = anIntArray1675;
        if (i6 == k6) {
            i6 = 0;
            k6 = anIntArray1673[11];
            auid = anIntArrayArray1674[11];
            ai3 = anIntArray1676;
        }
        int i5;
        if (i6 < k6)
            i5 = ai3[i6];
        else
            i5 = -1000;

        for (int l6 = 0; l6 < 10; l6++) {
            while (l6 == 0 && i5 > l2) {
                rasterize(auid[i6++]);
                if (i6 == k6 && auid != anIntArrayArray1674[11]) {
                    i6 = 0;
                    k6 = anIntArray1673[11];
                    auid = anIntArrayArray1674[11];
                    ai3 = anIntArray1676;
                }
                if (i6 < k6)
                    i5 = ai3[i6];
                else
                    i5 = -1000;
            }
            while (l6 == 3 && i5 > k3) {
                rasterize(auid[i6++]);
                if (i6 == k6 && auid != anIntArrayArray1674[11]) {
                    i6 = 0;
                    k6 = anIntArray1673[11];
                    auid = anIntArrayArray1674[11];
                    ai3 = anIntArray1676;
                }
                if (i6 < k6)
                    i5 = ai3[i6];
                else
                    i5 = -1000;
            }
            while (l6 == 5 && i5 > j4) {
                rasterize(auid[i6++]);
                if (i6 == k6 && auid != anIntArrayArray1674[11]) {
                    i6 = 0;
                    k6 = anIntArray1673[11];
                    auid = anIntArrayArray1674[11];
                    ai3 = anIntArray1676;
                }
                if (i6 < k6)
                    i5 = ai3[i6];
                else
                    i5 = -1000;
            }
            int i7 = anIntArray1673[l6];
            int ai4[] = anIntArrayArray1674[l6];
            for (int j7 = 0; j7 < i7; j7++)
                rasterize(ai4[j7]);

        }
        while (i5 != -1000) {
            rasterize(auid[i6++]);
            if (i6 == k6 && auid != anIntArrayArray1674[11]) {
                i6 = 0;
                auid = anIntArrayArray1674[11];
                k6 = anIntArray1673[11];
                ai3 = anIntArray1676;
            }
            if (i6 < k6)
                i5 = ai3[i6];
            else
                i5 = -1000;
        }
    }

    private final void rasterize(int face) {
        if (outOfReach[face]) {
            method485(face);
            return;
        }
        int j = triangle_edge_a[face];
        int k = triangle_edge_b[face];
        int l = triangle_edge_c[face];
        Rasterizer3D.testX = hasAnEdgeToRestrict[face];
        if (face_alpha == null)
            Rasterizer3D.alpha = 0;
        else
            Rasterizer3D.alpha = face_alpha[face] & 0xff;

        int type;
        if (render_type == null)
            type = 0;
        else
            type = render_type[face] & 3;

        if (!Rasterizer3D.forceRepeat) {
            if (repeatTexture == null) {
                Rasterizer3D.repeatTexture = false;
            } else {
                Rasterizer3D.repeatTexture = repeatTexture[face];
            }
        } else {
            Rasterizer3D.repeatTexture = true;
        }

        if (face_material != null && face_material[face] != -1) {
            int texture_a = j;
            int texture_b = k;
            int texture_c = l;
            if (face_texture != null && face_texture[face] != -1) {
                int coordinate = face_texture[face] & 0xff;
                texture_a = triangle_texture_edge_a[coordinate];
                texture_b = triangle_texture_edge_b[coordinate];
                texture_c = triangle_texture_edge_c[coordinate];
            }
            if (faceHslC[face] == -1 || type == 3) {
                Rasterizer3D.drawTexturedTriangle(
                    projected_vertex_y[j], projected_vertex_y[k], projected_vertex_y[l],
                    projected_vertex_x[j], projected_vertex_x[k], projected_vertex_x[l],
                    faceHslA[face], faceHslA[face], faceHslA[face],
                    camera_vertex_x[texture_a], camera_vertex_x[texture_b], camera_vertex_x[texture_c],
                    camera_vertex_y[texture_a], camera_vertex_y[texture_b], camera_vertex_y[texture_c],
                    camera_vertex_z[texture_a], camera_vertex_z[texture_b], camera_vertex_z[texture_c],
                    face_material[face]);
            } else {
                Rasterizer3D.drawTexturedTriangle(
                    projected_vertex_y[j], projected_vertex_y[k], projected_vertex_y[l],
                    projected_vertex_x[j], projected_vertex_x[k], projected_vertex_x[l],
                    faceHslA[face], faceHslB[face], faceHslC[face],
                    camera_vertex_x[texture_a], camera_vertex_x[texture_b], camera_vertex_x[texture_c],
                    camera_vertex_y[texture_a], camera_vertex_y[texture_b], camera_vertex_y[texture_c],
                    camera_vertex_z[texture_a], camera_vertex_z[texture_b], camera_vertex_z[texture_c],
                    face_material[face]);
            }
        } else {
            if (type == 0) {
                Rasterizer3D.drawShadedTriangle(projected_vertex_y[j], projected_vertex_y[k],
                    projected_vertex_y[l], projected_vertex_x[j], projected_vertex_x[k],
                    projected_vertex_x[l], faceHslA[face], faceHslB[face], faceHslC[face]);
                return;
            }
            if (type == 1) {
                Rasterizer3D.drawFlatTriangle(projected_vertex_y[j], projected_vertex_y[k], projected_vertex_y[l], projected_vertex_x[j], projected_vertex_x[k], projected_vertex_x[l], modelIntArray3[faceHslA[face]]);
                return;
            }
        }
    }

    private final void method485(int i) {
        int j = Rasterizer3D.center_x;
        int k = Rasterizer3D.center_y;
        int l = 0;
        int i1 = triangle_edge_a[i];
        int j1 = triangle_edge_b[i];
        int k1 = triangle_edge_c[i];
        int l1 = camera_vertex_z[i1];
        int uid = camera_vertex_z[j1];
        int j2 = camera_vertex_z[k1];
        if (l1 >= 50) {
            anIntArray1678[l] = projected_vertex_x[i1];
            anIntArray1679[l] = projected_vertex_y[i1];
            anIntArray1680[l++] = faceHslA[i];
        } else {
            int k2 = camera_vertex_x[i1];
            int k3 = camera_vertex_y[i1];
            int k4 = faceHslA[i];
            if (j2 >= 50) {
                int k5 = (50 - l1) * modelIntArray4[j2 - l1];
                anIntArray1678[l] = j + (k2 + ((camera_vertex_x[k1] - k2) * k5 >> 16) << SceneGraph.view_dist) / 50;
                anIntArray1679[l] = k + (k3 + ((camera_vertex_y[k1] - k3) * k5 >> 16) << SceneGraph.view_dist) / 50;
                anIntArray1680[l++] = k4 + ((faceHslC[i] - k4) * k5 >> 16);
            }
            if (uid >= 50) {
                int l5 = (50 - l1) * modelIntArray4[uid - l1];
                anIntArray1678[l] = j + (k2 + ((camera_vertex_x[j1] - k2) * l5 >> 16) << SceneGraph.view_dist) / 50;
                anIntArray1679[l] = k + (k3 + ((camera_vertex_y[j1] - k3) * l5 >> 16) << SceneGraph.view_dist) / 50;
                anIntArray1680[l++] = k4 + ((faceHslB[i] - k4) * l5 >> 16);
            }
        }
        if (uid >= 50) {
            anIntArray1678[l] = projected_vertex_x[j1];
            anIntArray1679[l] = projected_vertex_y[j1];
            anIntArray1680[l++] = faceHslB[i];
        } else {
            int l2 = camera_vertex_x[j1];
            int l3 = camera_vertex_y[j1];
            int l4 = faceHslB[i];
            if (l1 >= 50) {
                int i6 = (50 - uid) * modelIntArray4[l1 - uid];
                anIntArray1678[l] = j + (l2 + ((camera_vertex_x[i1] - l2) * i6 >> 16) << SceneGraph.view_dist) / 50;
                anIntArray1679[l] = k + (l3 + ((camera_vertex_y[i1] - l3) * i6 >> 16) << SceneGraph.view_dist) / 50;
                anIntArray1680[l++] = l4 + ((faceHslA[i] - l4) * i6 >> 16);
            }
            if (j2 >= 50) {
                int j6 = (50 - uid) * modelIntArray4[j2 - uid];
                anIntArray1678[l] = j + (l2 + ((camera_vertex_x[k1] - l2) * j6 >> 16) << SceneGraph.view_dist) / 50;
                anIntArray1679[l] = k + (l3 + ((camera_vertex_y[k1] - l3) * j6 >> 16) << SceneGraph.view_dist) / 50;
                anIntArray1680[l++] = l4 + ((faceHslC[i] - l4) * j6 >> 16);
            }
        }
        if (j2 >= 50) {
            anIntArray1678[l] = projected_vertex_x[k1];
            anIntArray1679[l] = projected_vertex_y[k1];
            anIntArray1680[l++] = faceHslC[i];
        } else {
            int i3 = camera_vertex_x[k1];
            int i4 = camera_vertex_y[k1];
            int i5 = faceHslC[i];
            if (uid >= 50) {
                int k6 = (50 - j2) * modelIntArray4[uid - j2];
                anIntArray1678[l] = j + (i3 + ((camera_vertex_x[j1] - i3) * k6 >> 16) << SceneGraph.view_dist) / 50;
                anIntArray1679[l] = k + (i4 + ((camera_vertex_y[j1] - i4) * k6 >> 16) << SceneGraph.view_dist) / 50;
                anIntArray1680[l++] = i5 + ((faceHslB[i] - i5) * k6 >> 16);
            }
            if (l1 >= 50) {
                int l6 = (50 - j2) * modelIntArray4[l1 - j2];
                anIntArray1678[l] = j + (i3 + ((camera_vertex_x[i1] - i3) * l6 >> 16) << SceneGraph.view_dist) / 50;
                anIntArray1679[l] = k + (i4 + ((camera_vertex_y[i1] - i4) * l6 >> 16) << SceneGraph.view_dist) / 50;
                anIntArray1680[l++] = i5 + ((faceHslA[i] - i5) * l6 >> 16);
            }
        }
        int j3 = anIntArray1678[0];
        int j4 = anIntArray1678[1];
        int j5 = anIntArray1678[2];
        int i7 = anIntArray1679[0];
        int j7 = anIntArray1679[1];
        int k7 = anIntArray1679[2];
        if ((j3 - j4) * (k7 - j7) - (i7 - j7) * (j5 - j4) > 0) {
            Rasterizer3D.testX = false;
            int texture_a = i1;
            int texture_b = j1;
            int texture_c = k1;
            if (l == 3) {
                if (j3 < 0 || j4 < 0 || j5 < 0 || j3 > Rasterizer2D.center_x || j4 > Rasterizer2D.center_x || j5 > Rasterizer2D.center_x)
                    Rasterizer3D.testX = true;

                int l7;
                if (render_type == null)
                    l7 = 0;
                else
                    l7 = render_type[i] & 3;

                if (face_material != null && face_material[i] != -1) {
                    if (face_texture != null && face_texture[i] != -1) {
                        int coordinate = face_texture[i] & 0xff;
                        texture_a = triangle_texture_edge_a[coordinate];
                        texture_b = triangle_texture_edge_b[coordinate];
                        texture_c = triangle_texture_edge_c[coordinate];
                    }
                    if (faceHslC[i] == -1) {
                        Rasterizer3D.drawTexturedTriangle(
                            i7, j7, k7,
                            j3, j4, j5,
                            faceHslA[i], faceHslA[i], faceHslA[i],
                            camera_vertex_x[texture_a], camera_vertex_x[texture_b], camera_vertex_x[texture_c],
                            camera_vertex_y[texture_a], camera_vertex_y[texture_b], camera_vertex_y[texture_c],
                            camera_vertex_z[texture_a], camera_vertex_z[texture_b], camera_vertex_z[texture_c],
                            face_material[i]);
                    } else {
                        Rasterizer3D.drawTexturedTriangle(
                            i7, j7, k7,
                            j3, j4, j5,
                            anIntArray1680[0], anIntArray1680[1], anIntArray1680[2],
                            camera_vertex_x[texture_a], camera_vertex_x[texture_b], camera_vertex_x[texture_c],
                            camera_vertex_y[texture_a], camera_vertex_y[texture_b], camera_vertex_y[texture_c],
                            camera_vertex_z[texture_a], camera_vertex_z[texture_b], camera_vertex_z[texture_c],
                            face_material[i]);
                    }
                } else {
                    if (l7 == 0)
                        Rasterizer3D.drawShadedTriangle(i7, j7, k7, j3, j4, j5, anIntArray1680[0], anIntArray1680[1], anIntArray1680[2]);

                    else if (l7 == 1)
                        Rasterizer3D.drawFlatTriangle(i7, j7, k7, j3, j4, j5, modelIntArray3[faceHslA[i]]);
                }
            }
            if (l == 4) {
                if (j3 < 0 || j4 < 0 || j5 < 0 || j3 > Rasterizer2D.center_x || j4 > Rasterizer2D.center_x || j5 > Rasterizer2D.center_x || anIntArray1678[3] < 0 || anIntArray1678[3] > Rasterizer2D.center_x)
                    Rasterizer3D.testX = true;
                int type;
                if (render_type == null)
                    type = 0;
                else
                    type = render_type[i] & 3;

                if (face_material != null && face_material[i] != -1) {
                    if (face_texture != null && face_texture[i] != -1) {
                        int coordinate = face_texture[i] & 0xff;
                        texture_a = triangle_texture_edge_a[coordinate];
                        texture_b = triangle_texture_edge_b[coordinate];
                        texture_c = triangle_texture_edge_c[coordinate];
                    }
                    if (faceHslC[i] == -1) {
                        Rasterizer3D.drawTexturedTriangle(
                            i7, j7, k7,
                            j3, j4, j5,
                            faceHslA[i], faceHslA[i], faceHslA[i],
                            camera_vertex_x[texture_a], camera_vertex_x[texture_b], camera_vertex_x[texture_c],
                            camera_vertex_y[texture_a], camera_vertex_y[texture_b], camera_vertex_y[texture_c],
                            camera_vertex_z[texture_a], camera_vertex_z[texture_b], camera_vertex_z[texture_c],
                            face_material[i]);
                        Rasterizer3D.drawTexturedTriangle(
                            i7, k7, anIntArray1679[3],
                            j3, j5, anIntArray1678[3],
                            faceHslA[i], faceHslA[i], faceHslA[i],
                            camera_vertex_x[texture_a], camera_vertex_x[texture_b], camera_vertex_x[texture_c],
                            camera_vertex_y[texture_a], camera_vertex_y[texture_b], camera_vertex_y[texture_c],
                            camera_vertex_z[texture_a], camera_vertex_z[texture_b], camera_vertex_z[texture_c],
                            face_material[i]);
                    } else {
                        Rasterizer3D.drawTexturedTriangle(
                            i7, j7, k7,
                            j3, j4, j5,
                            anIntArray1680[0], anIntArray1680[1], anIntArray1680[2],
                            camera_vertex_x[texture_a], camera_vertex_x[texture_b], camera_vertex_x[texture_c],
                            camera_vertex_y[texture_a], camera_vertex_y[texture_b], camera_vertex_y[texture_c],
                            camera_vertex_z[texture_a], camera_vertex_z[texture_b], camera_vertex_z[texture_c],
                            face_material[i]);
                        Rasterizer3D.drawTexturedTriangle(
                            i7, k7, anIntArray1679[3],
                            j3, j5, anIntArray1678[3],
                            anIntArray1680[0], anIntArray1680[2], anIntArray1680[3],
                            camera_vertex_x[texture_a], camera_vertex_x[texture_b], camera_vertex_x[texture_c],
                            camera_vertex_y[texture_a], camera_vertex_y[texture_b], camera_vertex_y[texture_c],
                            camera_vertex_z[texture_a], camera_vertex_z[texture_b], camera_vertex_z[texture_c],
                            face_material[i]);
                        return;
                    }
                } else {
                    if (type == 0) {
                        Rasterizer3D.drawShadedTriangle(i7, j7, k7, j3, j4, j5, anIntArray1680[0], anIntArray1680[1], anIntArray1680[2]);
                        Rasterizer3D.drawShadedTriangle(i7, k7, anIntArray1679[3], j3, j5, anIntArray1678[3], anIntArray1680[0], anIntArray1680[2], anIntArray1680[3]);
                        return;
                    }
                    if (type == 1) {
                        int l8 = modelIntArray3[faceHslA[i]];
                        Rasterizer3D.drawFlatTriangle(i7, j7, k7, j3, j4, j5, l8);
                        Rasterizer3D.drawFlatTriangle(i7, k7, anIntArray1679[3], j3, j5, anIntArray1678[3], l8);
                        return;
                    }
                }
            }
        }
    }

    private final boolean entered_clickbox(int mouse_x, int mouse_y, int y_a, int y_b, int y_c, int x_a, int x_b, int x_c) {
        if (mouse_y < y_a && mouse_y < y_b && mouse_y < y_c)
            return false;
        if (mouse_y > y_a && mouse_y > y_b && mouse_y > y_c)
            return false;
        if (mouse_x < x_a && mouse_x < x_b && mouse_x < x_c)
            return false;
        return mouse_x <= x_a || mouse_x <= x_b || mouse_x <= x_c;
    }


    public void method2593(int orientation) { //I believe this is calculateBoundingBox aka calculateExtreme
        //if (this.field1947 == -1) {
        int min_x = 0;
        int min_y = 0;
        int min_z = 0;
        int max_x = 0;
        int max_y = 0;
        int max_z = 0;
        int cos = COSINE[orientation];
        int sin = SINE[orientation];
        //get bounds
        for (int point = 0; point < this.vertices; point++) {
            int v_x = Rasterizer3D.calc_vertex_x(this.vertex_x[point], this.vertex_z[point], cos, sin);
            int v_y = this.vertex_y[point];
            int v_z = Rasterizer3D.calc_vertex_z(this.vertex_x[point], this.vertex_z[point], cos, sin);
            if (v_x < min_x) {
                min_x = v_x;
            }
            if (v_x > max_x) {
                max_x = v_x;
            }
            if (v_y < min_y) {
                min_y = v_y;
            }
            if (v_y > max_y) {
                max_y = v_y;
            }
            if (v_z < min_z) {
                min_z = v_z;
            }
            if (v_z > max_z) {
                max_z = v_z;
            }
        }
        //unsure
        this.field1944 = (max_x + min_x) / 2;
        this.field1963 = (max_y + min_y) / 2;
        this.field1946 = (max_z + min_z) / 2;
        this.field1947 = (max_x - min_x + 1) / 2;
        this.field1948 = (max_y - min_y + 1) / 2;
        this.field1924 = (max_z - min_z + 1) / 2;
        if (this.field1947 < 32) {
            this.field1947 = 32;
        }
        if (this.field1924 < 32) {
            this.field1924 = 32;
        }
        if (within_tile) {
            this.field1947 += 8;
            this.field1924 += 8;
        }
        //}
    }

    public int field1944;
    public int field1947;
    public int field1963;
    public int field1948;
    public int field1946;
    public int field1924;


    public int bufferOffset;
    public int uvBufferOffset;

    public int getBufferOffset() {
        return bufferOffset;
    }

    public void setBufferOffset(int bufferOffset) {
        this.bufferOffset = bufferOffset;
    }

    public int getUvBufferOffset() {
        return uvBufferOffset;
    }

    public void setUvBufferOffset(int uvBufferOffset) {
        this.uvBufferOffset = uvBufferOffset;
    }

    public float[][] getFaceTextureUCoordinates() {
        return new float[][]{};
    }

    public float[][] getFaceTextureVCoordinates() {
        return new float[][]{};
    }

    public void addTextureWithCoordinate(int[] originalIds, int[] targetIds, TextureCoordinate coordinate) {
        texture_faces = originalIds.length;
        triangle_texture_edge_a = new int[texture_faces];
        triangle_texture_edge_b = new int[texture_faces];
        triangle_texture_edge_c = new int[texture_faces];
        face_texture = new byte[faces];
        texture_map = new byte[texture_faces];
        face_material = new int[faces];
        if (render_type == null) {
            render_type = new int[faces];
        }

        for (int i = 0; i < texture_faces; i++) {
            this.triangle_texture_edge_a[i] = coordinate.getA();
            this.triangle_texture_edge_b[i] = coordinate.getB();
            this.triangle_texture_edge_c[i] = coordinate.getC();
        }

        /*this.triangle_texture_edge_a[1] = 12;
        this.triangle_texture_edge_b[1] = 4;
        this.triangle_texture_edge_c[1] = 1;*/

        Arrays.fill(face_texture, (byte) -1);
        for (int i = 0; i < this.faces; i++) {
            for (int index = 0; index < originalIds.length; index++) {
                if (this.face_color[i] == originalIds[index]) {
                    this.face_texture[i] = (byte) index;
                    this.render_type[i] = 2;
                    this.face_material[i] = (short) targetIds[index];
                }
            }
        }

        for (int i = 0; i < faces; i++) {
            if (face_texture[i] == -1) {
                face_material[i] = -1;
                render_type[i] = 0;
            }
        }
    }

    public void completelyRecolor(int j) {
        for (int k = 0; k < faces; k++) {
            face_color[k] = (short) j;
        }
    }

    public void shadingRecolor(int j) {
        j += 100;
        int kcolor = 0;
        for (int k = 0; k < faces; k++) {
            kcolor = face_color[k];
            if (k + j >= 0) {
                face_color[k] = (short) (kcolor + j);
            }
        }
    }

    public void shadingRecolor2(int j) {

        for (int k = 0; k < faces; k++) {
            if (k + j >= 0) {
                face_color[k] = (short) (k + j);
            }
        }
    }

    public void shadingRecolor4(int j) {

        for (int k = 0; k < faces; k++) {
            if (j == 222) {
                System.out.println("k = " + face_color[k]);
            }
            if ((face_color[k] != 65535) && (k + j >= 0)) {
                face_color[k] += j;
            }
        }
    }

    public void shadingRecolor3(int j) {

        for (int k = 0; k < faces; k++) {
            int lastcolor = 1;
            if ((face_color[k] + j >= 10000)
                && (face_color[k] + j <= 90000)) {
                face_color[k] = (short) (k + j + lastcolor);
            }
            lastcolor++;
        }
    }

    public void modelRecoloring(int i, int j) {
        for (int k = 0; k < faces; k++) {
            if (face_color[k] == i) {
                face_color[k] = (short) j;
            }
        }
    }

}
