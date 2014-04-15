/*
 * Copyright (C) 2011-2014, Peter Abeles. All Rights Reserved.
 *
 * This file is part of Geometric Regression Library (GeoRegression).
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package georegression.fitting.sphere;

import georegression.misc.GrlConstants;
import georegression.struct.point.Point3D_F64;
import org.ddogleg.optimization.JacobianChecker;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * @author Peter Abeles
 */
public class TestSphereToPointSignedDistanceJacobian_F64 {

	@Test
	public void compareToNumerical() {

		SphereToPointSignedDistance_F64 function = new SphereToPointSignedDistance_F64();
		SphereToPointSignedDistanceJacobian_F64 jacobian = new SphereToPointSignedDistanceJacobian_F64();

		// sphere
		/**/double param[] = new /**/double[]{1,2,3,4};

		List<Point3D_F64> points = new ArrayList<Point3D_F64>();

		// inside, should be negative
		points.add(new Point3D_F64(1.1,1.95,7.2));
		// outside, should be positive
		points.add(new Point3D_F64(0.96,-2.1,3.001));
		points.add(new Point3D_F64(5.2,2.05,3.1));

		function.setPoints(points);
		jacobian.setPoints(points);

//		JacobianChecker.jacobianPrint(function, jacobian, param, 100.0*GrlConstants.DOUBLE_TEST_TOL,
//				GrlConstants.DOUBLE_TEST_TOL);
		assertTrue(JacobianChecker.jacobian(function, jacobian, param, 100.0 * GrlConstants.DOUBLE_TEST_TOL,
				GrlConstants.DOUBLE_TEST_TOL));
	}

	@Test
	public void getN_and_getM() {
		SphereToPointSignedDistanceJacobian_F64 alg = new SphereToPointSignedDistanceJacobian_F64();

		List<Point3D_F64> points = new ArrayList<Point3D_F64>();
		points.add(new Point3D_F64(1,2,3.5));
		points.add(new Point3D_F64(1,2,3.5));
		points.add(new Point3D_F64(1,2,3.5));

		alg.setPoints(points);

		assertEquals(4,alg.getNumOfInputsN());
		assertEquals(points.size(),alg.getNumOfOutputsM());
	}
}