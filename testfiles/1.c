#include <stdio.h>

int getchar(){
char c;
scanf("%c",&c);
return (int)c;
}
int getint(){
int t;
scanf("%d",&t);
while(getchar()!='\n');
return t;
}

void foo() {
    int x = +-+9;
    if (x > 10) {
        x = 10;
    } else {
        x = -10;
        printf("damn\n");
    }

    if (x > -100) {
        x = 2;
    }
    printf("final x here is %d\n", x);
    return;
}

void cs2()
{
    int i=0, j = 1, k;
    for (;i <= 7;)
    {
        i = i + 1;;;;;;;;;
        i+1;i+2;i;i+7;
        if (j == i)
        {
            k = k + j;
            continue;
        }
        if (j != i) {}
        if (j >= i) {}
        if (j > i) {}
        if (j < i) {}
        if (j <= i) {}
        if (!(j != i)) {j = -9;}
        j = j - 1;
    }
    return;
}

int bar(int a, int b, int c, char ch) {
    int sum;
    if (ch == 'a') {
        sum = a + b - c;
    }
    int i = -9;
    for (i = 0; i < c; i = i + 1) {
        sum = sum - b;
        if (i == 0) {
            break;
        }
    }
    return sum;
}

char man(int i) {
    if (i == 1) {
        return 'm';
    } else {
        return 'n';
    }
    return 'a';
}

int bar1(int a) {
    return a + 1;
}

int main() {
    int x = 9;
    const int abc = 9 + 3 - 6 * 3 / 5;
    int y = 5 % 4;
    int z = x, zz = (y + y * y), zzz;

    // youdiane!
    /* find something to eat */
    char c1 = 'c';
    const char c2 = 'b';
    char c3 = 's', c4, c5 = c2, c6 = c1 + c2;
    char c7 = (c2);

    int zxw = 9;
    if (1) {
        zxw = 22373498;
    }
    printf("%d\n", zxw);
    
    int k = 9;
    int kk = 79;
    for (;;) {
        k = k + 1;
        if (k == 10) {
            continue;
        }
        if (k == 12) {
            break;
        }
        kk = kk - 1;
    }
    {
        int y = 114514;
        printf("%d\n", y);
        {
            {}{{y + 9;printf("%d is here!\n", y);}}
        }
    }

    int bzh;bzh = getint();
    char zlr;zlr = getchar();

    printf("%c%c%d%c\n", zlr, man(bzh), bzh, man(1));
    printf("wobuxiangchifan\n");

    int i = 0, a;
    for(i = 0;;){
        break;
    }
    for(;; i = i + 1000){
        break;
    }
    for(; i < 22373498;){
        i = i + 10000000;
        break;
    }
    
    for(i = 0; i < 3; i = i + 1){
        int j = 2;
        for (j=0;j<4;j = j + 2) {
            continue;
        }
        continue;
        a = 0;
    }
    for(i = -2; i < 5;){printf("bye\n");
        break;
    }
    for(i = 240819;; i = i + 1){
        break;
    }
    for(; i < 100; i = i + 1919810){
        break;
    }

    int res = bar(1, 2, 3,'a');
    foo();
    cs2();
    printf("I love this%dguys\n", bar(3, -4, 2, 'b'));
    printf("foolish %d\n", bar1(bar1(res)));
    return 0;
}