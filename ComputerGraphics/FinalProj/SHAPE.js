var SHAPE = (function() {
   var my = {};

   function addMeshVertices(V, m, n, func) {
      function append(A) {
         for (let i = 0 ; i < A.length ; i++)
            V.push(A[i]);
      }
      for (let j = 0 ; j < n ; j++)
      for (let i = 0 ; i < m ; i++) {
         let A = func( i   /m,  j   /n),
	     B = func((i+1)/m,  j   /n),
             C = func( i   /m, (j+1)/n),
	     D = func((i+1)/m, (j+1)/n);
         append(A); append(B); append(D); // Lower right of square.
         append(D); append(C); append(A); // Upper left of square.
      }
      return V;
   }

   function addDiskVertices(V, n, zSign, z) {
      function f(i) {
         let theta = zSign * 2 * Math.PI * i / n;
	 V.push(Math.cos(theta),Math.sin(theta),z, 0,0,zSign, 0,0);
      }
      for (let i = 0 ; i < n ; i++) {
         f(i  );
         f(i+1);
         V.push(0,0,z, 0,0,zSign, 0,0);
      }
      return V;
   }
 my.tube = function(u, v) {
      var theta = 2 * Math.PI * u;
      return [ Math.cos(theta),
               Math.sin(theta),
          2 * v - 1 ];
   }
   function addTubeVertices(V, n) {
      return addMeshVertices(V, n, 1, function(u, v) {
         let theta = 2 * Math.PI * u;
         let z     = 2 * v - 1;
         let c     = Math.cos(theta);
         let s     = Math.sin(theta);
         return [c,s,z, c,s,0, u,v];
      });
   }
   my.cylinder = function(n) {
      var V = [];
      addDiskVertices(V, n, -1, -1);
      addTubeVertices(V, n);
      addDiskVertices(V, n,  1,  1);
      return V;
   }
   function sphereVertices(V,n){
      return addMeshVertices(V, n, 1, function(u, v) {
         let theta = 2 * Math.PI * u;
         let phi = Math.PI * (v-.5);
         let x = Math.cos(theta)*Math.cos(phi);
         let y = Math.sin(theta)*Math.cos(phi);
         let z = Math.sin(phi);
         return [x,y,z, x,y,0, u,v];
      });
   }
//my attempt at matching his cylinder thing with a sphere tho
   my.sphere = function(n){
      var V=[];
      sphereVertices(V,n);
      return V;
   }

   my._quad = function(f, u0, v0, u1, v1) {
      return [
         f(u0, v0),
         f(u1, v0),
         f(u1, v1),
         f(u0, v1),
         f(u0, v0)
      ];
   }

   my.parametricMesh = function(f, nu, nv) {
      var i, j, u, v, C = [];
      for (j = 0 ; j < nv ; j++) {
         v = j / nv;
         for (i = 0 ; i < nu ; i++) {
            u = i / nu;
       C.push(my._quad(f, u, v, u + 1/nu, v + 1/nv));
         }
      }
      return C;
   }

   my.torus = function(u,v){
      var theta = 2 * Math.PI * u;
      var phi = 2 * Math.PI * v;
      var r = 0.3;
      return [Math.cos(theta)*(1+r*Math.cos(phi)), Math.sin(theta)*(1+r*Math.cos(phi)), r*Math.sin(phi)];
   }

   my.halfTorus = function(u,v){
      var theta = 1 *Math.PI * u;
      var phi = 2 * Math.PI * v;
      var r = 0.3;
      return [Math.cos(theta)*(1+r*Math.cos(phi)), Math.sin(theta)*(1+r*Math.cos(phi)), r*Math.sin(phi)];
   }

   my.halfSphere = function(u, v){
      var theta = Math.PI * u;
      var phi = Math.PI * (v-.5);
      return [Math.cos(theta)*Math.cos(phi), Math.sin(theta)*Math.cos(phi), Math.sin(phi)];
   }

   return my;
})();

