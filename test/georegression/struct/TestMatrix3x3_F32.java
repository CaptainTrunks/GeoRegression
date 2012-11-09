/*
 * Copyright (c) 2011-2012, Peter Abeles. All Rights Reserved.
 *
 * This file is part of Geometric Regression Library (GeoRegression).
 *
 * GeoRegression is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation, either version 3
 * of the License, or (at your option) any later version.
 *
 * GeoRegression is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with GeoRegression.  If not, see <http://www.gnu.org/licenses/>.
 */

package georegression.struct;

import georegression.misc.GrlConstants;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * @author Peter Abeles
 */
public class TestMatrix3x3_F32 {

	@Test
	public void set_matrix() {
		Matrix3x3_F32 a = new Matrix3x3_F32();
		a.set(1, 2, 3, 4, 5, 6, 7, 8, 9);

		Matrix3x3_F32 m = new Matrix3x3_F32();
		m.set(a);

		assertTrue(m.a11 == 1);
		assertTrue(m.a12 == 2);
		assertTrue(m.a13 == 3);
		assertTrue(m.a21 == 4);
		assertTrue(m.a22 == 5);
		assertTrue(m.a23 == 6);
		assertTrue(m.a31 == 7);
		assertTrue(m.a32 == 8);
		assertTrue(m.a33 == 9);
	}

	@Test
	public void set_values() {
		Matrix3x3_F32 m = new Matrix3x3_F32();
		m.set(1,2,3,4,5,6,7,8,9);

		assertTrue(m.a11 == 1);
		assertTrue(m.a12 == 2);
		assertTrue(m.a13 == 3);
		assertTrue(m.a21 == 4);
		assertTrue(m.a22 == 5);
		assertTrue(m.a23 == 6);
		assertTrue(m.a31 == 7);
		assertTrue(m.a32 == 8);
		assertTrue(m.a33 == 9);
	}

	@Test
	public void scale() {
		Matrix3x3_F32 m = new Matrix3x3_F32();
		m.set(1,2,3,4,5,6,7,8,9);
		m.scale(2);

		assertEquals(2,m.a11, GrlConstants.FLOAT_TEST_TOL);
		assertEquals(4,m.a12, GrlConstants.FLOAT_TEST_TOL);
		assertEquals(6,m.a13, GrlConstants.FLOAT_TEST_TOL);
		assertEquals(8,m.a21, GrlConstants.FLOAT_TEST_TOL);
		assertEquals(10,m.a22, GrlConstants.FLOAT_TEST_TOL);
		assertEquals(12,m.a23, GrlConstants.FLOAT_TEST_TOL);
		assertEquals(14,m.a31, GrlConstants.FLOAT_TEST_TOL);
		assertEquals(16,m.a32, GrlConstants.FLOAT_TEST_TOL);
		assertEquals(18,m.a33, GrlConstants.FLOAT_TEST_TOL);
	}
}
