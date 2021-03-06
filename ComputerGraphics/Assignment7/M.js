
//////////////////////////////////////////////////////////////////////////////
// M is an object containing methods that let you manipulate 4x4 matrices.
//////////////////////////////////////////////////////////////////////////////

var M = {};
M._stack=[];

//////////////////////////////////////////////////////////////////////////////
// Your task is to implement the following methods of object M:
//////////////////////////////////////////////////////////////////////////////

M.identity  = function(m)          {
  id =   [1,0,0,0,
          0,1,0,0,
          0,0,1,0,
          0,0,0,1];
  for (i = 0; i<16 ; i++){
    m[i] = id[i];
  }
} // Set m values to identity matrix.
M.restore   = function(m)          {
  var i, m2 = M._stack.pop();
 
  for (i = 0; i<m.length; i++){
    m[i] = m2[i];
  }
} // Pop from a stack to set the 16 values of m.
M.rotateX   = function(m, radians) {
  temp=[1,0,0,0,
        0,Math.cos(radians),-Math.sin(radians),0,
        0,Math.sin(radians),Math.cos(radians),0,
        0,0,0,1];
  M.matrixMultiply(m,temp,m);   
} // Modify m, rotating about the X axis.

M.rotateY   = function(m, radians) {
  temp=[Math.cos(radians),0,Math.sin(radians),0,
        0,1,0,0,
        -Math.sin(radians),0,Math.cos(radians),0,
        0,0,0,1];
  M.matrixMultiply(m,temp,m);
} // Modify m, rotating about the Y axis.
M.rotateZ   = function(m, radians) {
  temp=[Math.cos(radians),-Math.sin(radians),0,0,
      Math.sin(radians),Math.cos(radians),0,0,
      0,0,1,0,
      0,0,0,1];
  M.matrixMultiply(m,temp,m);    
} // Modify m, rotating about the Z axis.
M.save      = function(m)          {
  
  var m2 = [];
  for (i = 0; i < 16 ; i++){
    m2.push(m[i]);
  }
  M._stack.push(m2);
} // Push the 16 values of m onto a stack.
M.scale = function(m, v) {
  var x,y,z;

  if (v instanceof Array) {
     x = v[0];
     y = v[1];
     z = v[2];
  }
  else
     x = y = z = v;

  mult = [x,0,0,0, 0,y,0,0, 0,0,z,0, 0,0,0,1];
  M.matrixMultiply(m,mult,m);
} // Modify m, scaling by v[0],v[1],v[2].
M.transform = function(m, v)       { return m; } // Return vec v transformed by matrix m.
M.translate = function(m, v)       {           } // Modify m, translating by v[0],v[1],v[2].

//////////////////////////////////////////////////////////////////////////////
// I have given you a head start by implementing some of the methods for you.
//
// Notice how I use M.matrixMultiply() to help implement other methods.
//////////////////////////////////////////////////////////////////////////////

M.translate = function(m, v) {
   M.matrixMultiply(m, M.translationMatrix(v), m);
}

M.translationMatrix = function(v) {
   return [ 1,0,0,0, 0,1,0,0, 0,0,1,0, v[0],v[1],v[2],1 ];
}

M.matrixMultiply = function(a, b, dst) {
   var n, tmp = []; 

   // PUT THE RESULT OF a x b INTO TEMPORARY MATRIX tmp.

   for (n = 0 ; n < 16 ; n++)
      tmp.push( a[n&3     ] * b[    n&12] +
                a[n&3 |  4] * b[1 | n&12] +
                a[n&3 |  8] * b[2 | n&12] +
                a[n&3 | 12] * b[3 | n&12] );

   // COPY tmp VALUES INTO DESTINATION MATRIX dst.

   for (n = 0 ; n < 16 ; n++)
      dst[n] = tmp[n];
}

M.transform = function(m, v)  {

    // IF v[3] IS UNDEFINED, SET IT TO 1 (THAT IS, ASSUME v IS A POINT).

    var x = v[0], y = v[1], z = v[2], w = v[3] === undefined ? 1 : v[3];

    // RETURN RESULT OF TRANSFORMING v BY MATRIX m.

    return [ x * m[0] + y * m[4] + z * m[ 8] + w * m[12],
             x * m[1] + y * m[5] + z * m[ 9] + w * m[13],
             x * m[2] + y * m[6] + z * m[10] + w * m[14],
             x * m[3] + y * m[7] + z * m[11] + w * m[15] ];
}

