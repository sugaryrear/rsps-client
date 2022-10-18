package com.ferox.net;

public final class IsaacCipher {

    public IsaacCipher(int[] seed) {
        memory = new int[256];
        results = new int[256];
        System.arraycopy(seed, 0, results, 0, seed.length);
        init();
    }

    public int value() {
        if (count-- == 0) {
            isaac();
            count = 255;
        }
        return results[count];
    }

    private void isaac() {
        this.prev += ++counter;
        for (int size = 0; size < 256; size++) {
            int pos = this.memory[size];
            if ((size & 3) == 0)
                this.accumulator ^= this.accumulator << 13;
            else if ((size & 3) == 1)
                this.accumulator ^= this.accumulator >>> 6;
            else if ((size & 3) == 2)
                this.accumulator ^= this.accumulator << 2;
            else if ((size & 3) == 3)
                this.accumulator ^= this.accumulator >>> 16;

            this.accumulator += this.memory[size + 128 & 0xff];
            int k;
            this.memory[size] = k = this.memory[(pos & 0x3fc) >> 2] + this.accumulator + this.prev;
            this.results[size] = this.prev = this.memory[(k >> 8 & 0x3fc) >> 2] + pos;
        }
    }

    private void init() {
        int a;
        int b;
        int c;
        int d;
        int e;
        int f;
        int g;
        int h = a = b = c = d = e = f = g = 0x9e3779b9;
        for (int pos = 0; pos < 4; pos++) {
            h ^= a << 11;
            c += h;
            a += b;
            a ^= b >>> 2;
            d += a;
            b += c;
            b ^= c << 8;
            e += b;
            c += d;
            c ^= d >>> 16;
            f += c;
            d += e;
            d ^= e << 10;
            g += d;
            e += f;
            e ^= f >>> 4;
            h += e;
            f += g;
            f ^= g << 8;
            a += f;
            g += h;
            g ^= h >>> 9;
            b += g;
            h += a;
        }
        for (int pos = 0; pos < 256; pos += 8) {
            h += this.results[pos];
            a += this.results[pos + 1];
            b += this.results[pos + 2];
            c += this.results[pos + 3];
            d += this.results[pos + 4];
            e += this.results[pos + 5];
            f += this.results[pos + 6];
            g += this.results[pos + 7];
            h ^= a << 11;
            c += h;
            a += b;
            a ^= b >>> 2;
            d += a;
            b += c;
            b ^= c << 8;
            e += b;
            c += d;
            c ^= d >>> 16;
            f += c;
            d += e;
            d ^= e << 10;
            g += d;
            e += f;
            e ^= f >>> 4;
            h += e;
            f += g;
            f ^= g << 8;
            a += f;
            g += h;
            g ^= h >>> 9;
            b += g;
            h += a;
            this.memory[pos] = h;
            this.memory[pos + 1] = a;
            this.memory[pos + 2] = b;
            this.memory[pos + 3] = c;
            this.memory[pos + 4] = d;
            this.memory[pos + 5] = e;
            this.memory[pos + 6] = f;
            this.memory[pos + 7] = g;
        }

        for (int size = 0; size < 256; size += 8) {
            h += this.memory[size];
            a += this.memory[size + 1];
            b += this.memory[size + 2];
            c += this.memory[size + 3];
            d += this.memory[size + 4];
            e += this.memory[size + 5];
            f += this.memory[size + 6];
            g += this.memory[size + 7];
            h ^= a << 11;
            c += h;
            a += b;
            a ^= b >>> 2;
            d += a;
            b += c;
            b ^= c << 8;
            e += b;
            c += d;
            c ^= d >>> 16;
            f += c;
            d += e;
            d ^= e << 10;
            g += d;
            e += f;
            e ^= f >>> 4;
            h += e;
            f += g;
            f ^= g << 8;
            a += f;
            g += h;
            g ^= h >>> 9;
            b += g;
            h += a;
            this.memory[size] = h;
            this.memory[size + 1] = a;
            this.memory[size + 2] = b;
            this.memory[size + 3] = c;
            this.memory[size + 4] = d;
            this.memory[size + 5] = e;
            this.memory[size + 6] = f;
            this.memory[size + 7] = g;
        }

        this.isaac();
        this.count = 256;
    }

    private int count;
    private final int[] results;
    private final int[] memory;
    private int accumulator;
    private int prev;
    private int counter;
}
