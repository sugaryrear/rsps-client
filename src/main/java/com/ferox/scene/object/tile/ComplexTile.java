package com.ferox.scene.object.tile;

public final class ComplexTile
{

    public ComplexTile(int tile_z, int l_sw, int hsl_nw, int v_ne, int material, int l_ne, int rotation,
                       int hsl_sw, int underlay_color, int hsl_ne, int v_nw, int v_se, int v_sw, int shape,
                       int l_nw, int l_se, int hsl_se, int tile_x, int minimap_color)
    {
        flat = !(v_sw != v_se || v_sw != v_ne || v_sw != v_nw);
        this.shape = shape;
        this.rotation = rotation;
        this.underlay_color = underlay_color;
        this.minimap_color = minimap_color;
        char c_512 = '\200';
        int c_256 = c_512 / 2;
        int c_128 = c_512 / 4;
        int c_384 = (c_512 * 3) / 4;
        int mesh[] = shaped_tile_vertex_data[shape];
        int length = mesh.length;
        vertex_x = new int[length];
        vertex_y = new int[length];
        vertex_z = new int[length];
        int vertex_color_overlay[] = new int[length];
        int vertex_color_underlay[] = new int[length];
        int x512 = tile_x * c_512;
        int z512 = tile_z * c_512;
        for(int vertex = 0; vertex < length; vertex++)
        {
            int opcode = mesh[vertex];
            if((opcode & 1) == 0 && opcode <= 8)
                opcode = (opcode - rotation - rotation - 1 & 7) + 1;

            if(opcode > 8 && opcode <= 12)
                opcode = (opcode - 9 - rotation & 3) + 9;

            if(opcode > 12 && opcode <= 16)
                opcode = (opcode - 13 - rotation & 3) + 13;

            int v_x;
            int v_y;
            int v_z;
            int v_color_overlay;
            int v_color_underlay;
            if(opcode == 1) {
                v_x = x512;
                v_y = z512;
                v_z = v_sw;
                v_color_overlay = hsl_sw;
                v_color_underlay = l_sw;
            } else
            if(opcode == 2) {
                v_x = x512 + c_256;
                v_y = z512;
                v_z = v_sw + v_se >> 1;
                v_color_overlay = hsl_sw + hsl_se >> 1;
                v_color_underlay = l_sw + l_se >> 1;
            } else
            if(opcode == 3) {
                v_x = x512 + c_512;
                v_y = z512;
                v_z = v_se;
                v_color_overlay = hsl_se;
                v_color_underlay = l_se;
            } else
            if(opcode == 4) {
                v_x = x512 + c_512;
                v_y = z512 + c_256;
                v_z = v_se + v_ne >> 1;
                v_color_overlay = hsl_se + hsl_ne >> 1;
                v_color_underlay = l_se + l_ne >> 1;
            } else
            if(opcode == 5) {
                v_x = x512 + c_512;
                v_y = z512 + c_512;
                v_z = v_ne;
                v_color_overlay = hsl_ne;
                v_color_underlay = l_ne;
            } else
            if(opcode == 6) {
                v_x = x512 + c_256;
                v_y = z512 + c_512;
                v_z = v_ne + v_nw >> 1;
                v_color_overlay = hsl_ne + hsl_nw >> 1;
                v_color_underlay = l_ne + l_nw >> 1;
            } else
            if(opcode == 7) {
                v_x = x512;
                v_y = z512 + c_512;
                v_z = v_nw;
                v_color_overlay = hsl_nw;
                v_color_underlay = l_nw;
            } else
            if(opcode == 8) {
                v_x = x512;
                v_y = z512 + c_256;
                v_z = v_nw + v_sw >> 1;
                v_color_overlay = hsl_nw + hsl_sw >> 1;
                v_color_underlay = l_nw + l_sw >> 1;
            } else
            if(opcode == 9) {
                v_x = x512 + c_256;
                v_y = z512 + c_128;
                v_z = v_sw + v_se >> 1;
                v_color_overlay = hsl_sw + hsl_se >> 1;
                v_color_underlay = l_sw + l_se >> 1;
            } else
            if(opcode == 10) {
                v_x = x512 + c_384;
                v_y = z512 + c_256;
                v_z = v_se + v_ne >> 1;
                v_color_overlay = hsl_se + hsl_ne >> 1;
                v_color_underlay = l_se + l_ne >> 1;
            } else
            if(opcode == 11) {
                v_x = x512 + c_256;
                v_y = z512 + c_384;
                v_z = v_ne + v_nw >> 1;
                v_color_overlay = hsl_ne + hsl_nw >> 1;
                v_color_underlay = l_ne + l_nw >> 1;
            } else
            if(opcode == 12) {
                v_x = x512 + c_128;
                v_y = z512 + c_256;
                v_z = v_nw + v_sw >> 1;
                v_color_overlay = hsl_nw + hsl_sw >> 1;
                v_color_underlay = l_nw + l_sw >> 1;
            } else
            if(opcode == 13) {
                v_x = x512 + c_128;
                v_y = z512 + c_128;
                v_z = v_sw;
                v_color_overlay = hsl_sw;
                v_color_underlay = l_sw;
            } else
            if(opcode == 14) {
                v_x = x512 + c_384;
                v_y = z512 + c_128;
                v_z = v_se;
                v_color_overlay = hsl_se;
                v_color_underlay = l_se;
            } else
            if(opcode == 15) {
                v_x = x512 + c_384;
                v_y = z512 + c_384;
                v_z = v_ne;
                v_color_overlay = hsl_ne;
                v_color_underlay = l_ne;
            } else {
                v_x = x512 + c_128;
                v_y = z512 + c_384;
                v_z = v_nw;
                v_color_overlay = hsl_nw;
                v_color_underlay = l_nw;
            }
            vertex_x[vertex] = v_x;
            vertex_y[vertex] = v_z;
            vertex_z[vertex] = v_y;
            vertex_color_overlay[vertex] = v_color_overlay;
            vertex_color_underlay[vertex] = v_color_underlay;
        }

        int elements[] = shaped_tile_element_data[shape];
        int vertices = elements.length / 4;
        triangle_a = new int[vertices];
        triangle_b = new int[vertices];
        triangle_c = new int[vertices];
        hue_a = new int[vertices];
        hue_b = new int[vertices];
        hue_c = new int[vertices];
        if(material != -1) {
            this.material = new int[vertices];
        }
        int offset = 0;
        for(int vertex = 0; vertex < vertices; vertex++) {
            int type = elements[offset];
            int a = elements[offset + 1];
            int b = elements[offset + 2];
            int c = elements[offset + 3];
            offset += 4;
            if(a < 4)
                a = a - rotation & 3;

            if(b < 4)
                b = b - rotation & 3;

            if(c < 4)
                c = c - rotation & 3;

            triangle_a[vertex] = a;
            triangle_b[vertex] = b;
            triangle_c[vertex] = c;
            if(type == 0) {
                hue_a[vertex] = vertex_color_overlay[a];
                hue_b[vertex] = vertex_color_overlay[b];
                hue_c[vertex] = vertex_color_overlay[c];
                if(this.material != null)
                    this.material[vertex] = -1;
            } else {
                hue_a[vertex] = vertex_color_underlay[a];
                hue_b[vertex] = vertex_color_underlay[b];
                hue_c[vertex] = vertex_color_underlay[c];
                if(this.material != null)
                    this.material[vertex] = material;
            }
        }
        int y_a_offset = v_sw;
        int y_b_offset = v_se;
        if(v_se < y_a_offset)
            y_a_offset = v_se;

        if(v_se > y_b_offset)
            y_b_offset = v_se;

        if(v_ne < y_a_offset)
            y_a_offset = v_ne;

        if(v_ne > y_b_offset)
            y_b_offset = v_ne;

        if(v_nw < y_a_offset)
            y_a_offset = v_nw;

        if(v_nw > y_b_offset)
            y_b_offset = v_nw;

        y_a_offset /= 14;
        y_b_offset /= 14;
    }

    public final int[] vertex_x;
    public final int[] vertex_y;
    public final int[] vertex_z;
    public final int[] hue_a;
    public final int[] hue_b;
    public final int[] hue_c;
    public final int[] triangle_a;
    public final int[] triangle_b;
    public final int[] triangle_c;
    public int material[];
    public final boolean flat;
    public final int shape;
    public final int rotation;
    public final int underlay_color;
    public final int minimap_color;
    public static final int[] vertex_viewpoint_x = new int[6];
    public static final int[] vertex_viewpoint_y = new int[6];
    public static final int[] texture_viewpoint_x = new int[6];
    public static final int[] texture_viewpoint_y = new int[6];
    public static final int[] texture_viewpoint_z = new int[6];
    public static final int[] vertex_viewpoint_z = new int[6];
    /*static final int[] anIntArray693 = {//unused
        1, 0
    };
    static final int[] anIntArray694 = {
        2, 1
    };
    static final int[] anIntArray695 = {
        3, 3
    };*/
    private static final int[][] shaped_tile_vertex_data = {
        {
            1, 3, 5, 7
        }, {
        1, 3, 5, 7
    }, {
        1, 3, 5, 7
    }, {
        1, 3, 5, 7, 6
    }, {
        1, 3, 5, 7, 6
    }, {
        1, 3, 5, 7, 6
    }, {
        1, 3, 5, 7, 6
    }, {
        1, 3, 5, 7, 2, 6
    }, {
        1, 3, 5, 7, 2, 8
    }, {
        1, 3, 5, 7, 2, 8
    }, {
        1, 3, 5, 7, 11, 12
    }, {
        1, 3, 5, 7, 11, 12
    }, {
        1, 3, 5, 7, 13, 14
    }
    };
    private static final int[][] shaped_tile_element_data = {
        {
            0, 1, 2, 3, 0, 0, 1, 3
        }, {
        1, 1, 2, 3, 1, 0, 1, 3
    }, {
        0, 1, 2, 3, 1, 0, 1, 3
    }, {
        0, 0, 1, 2, 0, 0, 2, 4, 1, 0,
        4, 3
    }, {
        0, 0, 1, 4, 0, 0, 4, 3, 1, 1,
        2, 4
    }, {
        0, 0, 4, 3, 1, 0, 1, 2, 1, 0,
        2, 4
    }, {
        0, 1, 2, 4, 1, 0, 1, 4, 1, 0,
        4, 3
    }, {
        0, 4, 1, 2, 0, 4, 2, 5, 1, 0,
        4, 5, 1, 0, 5, 3
    }, {
        0, 4, 1, 2, 0, 4, 2, 3, 0, 4,
        3, 5, 1, 0, 4, 5
    }, {
        0, 0, 4, 5, 1, 4, 1, 2, 1, 4,
        2, 3, 1, 4, 3, 5
    }, {
        0, 0, 1, 5, 0, 1, 4, 5, 0, 1,
        2, 4, 1, 0, 5, 3, 1, 5, 4, 3,
        1, 4, 2, 3
    }, {
        1, 0, 1, 5, 1, 1, 4, 5, 1, 1,
        2, 4, 0, 0, 5, 3, 0, 5, 4, 3,
        0, 4, 2, 3
    }, {
        1, 0, 5, 4, 1, 0, 1, 5, 0, 0,
        4, 3, 0, 4, 5, 3, 0, 5, 2, 3,
        0, 1, 2, 5
    }
    };

}
