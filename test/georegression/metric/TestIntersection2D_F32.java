/*
 * Copyright (c) 2011, Peter Abeles. All Rights Reserved.
 *
 * This file is part of Geometric Regression Library (GeoRegression).
 *
 * GeoRegression is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation, either version 3
 * of the License, or (at your option) any later version.
 *
 * JGRL is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with GeoRegression.  If not, see <http://www.gnu.org/licenses/>.
 */

package georegression.metric;

import georegression.misc.autocode.JgrlConstants;
import georegression.struct.line.LineParametric2D_F32;
import georegression.struct.line.LineSegment2D_F32;
import georegression.struct.point.Point2D_F32;
import georegression.struct.se.Se2_F32;
import georegression.transform.se.SePointOps_F32;
import org.junit.Test;

import java.util.Random;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;


/**
 * @author Peter Abeles
 */
public class TestIntersection2D_F32 {
	Random rand = new Random( 234 );


	@Test
	public void intersection_ls_to_ls() {
		// check positive, none pathological cases
		checkIntersection( new LineSegment2D_F32( 0, 2, 2, 2 ), new LineSegment2D_F32( 2, 0, 2, 3 ), new Point2D_F32( 2, 2 ) );
		checkIntersection( new LineSegment2D_F32( 0, 2, 2, 0 ), new LineSegment2D_F32( 0, 0, 2, 2 ), new Point2D_F32( 1, 1 ) );

		// check boundary conditions
		checkIntersection( new LineSegment2D_F32( 0, 2, 2, 2 ), new LineSegment2D_F32( 0, 0, 0, 2 ), new Point2D_F32( 0, 2 ) );
		checkIntersection( new LineSegment2D_F32( 0, 2, 2, 2 ), new LineSegment2D_F32( 2, 0, 2, 2 ), new Point2D_F32( 2, 2 ) );
		checkIntersection( new LineSegment2D_F32( 1, 0, 1, 2 ), new LineSegment2D_F32( 0, 0, 2, 0 ), new Point2D_F32( 1, 0 ) );

		// check negative
		checkIntersection( new LineSegment2D_F32( 0, 2, 2, 2 ), new LineSegment2D_F32( 0, 0, 0, 1.9f ), null );
		checkIntersection( new LineSegment2D_F32( 0, 2, 2, 2 ), new LineSegment2D_F32( 2, 0, 2, 1.9f ), null );
		checkIntersection( new LineSegment2D_F32( 1, 0.1f, 1, 2 ), new LineSegment2D_F32( 0, 0, 2, 0 ), null );
	}

	public void checkIntersection( LineSegment2D_F32 a, LineSegment2D_F32 b, Point2D_F32 expected ) {
		Point2D_F32 found = Intersection2D_F32.intersection( a, b, null );
		if( found == null )
			assertTrue( expected == null );
		else {
			assertEquals( found.getX(), expected.getX(), JgrlConstants.FLOAT_TEST_TOL );
			assertEquals( found.getY(), expected.getY(), JgrlConstants.FLOAT_TEST_TOL );
		}

	}


	/**
	 * Checks to see if the expected distance is returned and that the end points of the
	 * line segment are respected.  The test cases are rotated around in a circle to test
	 * more geometric configurations
	 */
	@Test
	public void intersection_p_to_ls() {
		LineParametric2D_F32 paraLine = new LineParametric2D_F32();
		LineSegment2D_F32 target = new LineSegment2D_F32( -1, 1, 1, 1 );

		Se2_F32 tran = new Se2_F32();

		// rotate it in a circle to check more geometric configurations
		for( int i = 0; i < 20; i++ ) {

			tran.setTranslation( (float)rand.nextGaussian(), (float)rand.nextGaussian() );
			tran.setYaw( (float) ( 2 * (float)Math.PI * i / 20 ) );

			checkIntersection_p_to_ls( paraLine, target, tran );
		}
	}

	private void checkIntersection_p_to_ls( LineParametric2D_F32 paraLine,
											LineSegment2D_F32 target,
											Se2_F32 tran ) {
		// create a copy so the original isn't modified
		paraLine = paraLine.copy();
		target = target.copy();

		// apply the transform to the two lines
		paraLine.setPoint( SePointOps_F32.transform( tran, paraLine.getPoint(), null ) );

		target.setA( SePointOps_F32.transform( tran, target.getA(), null ) );
		target.setB( SePointOps_F32.transform( tran, target.getB(), null ) );

		// should hit it dead center
		paraLine.setSlope( 0, 1 );
		paraLine.setAngle( paraLine.getAngle() + tran.getYaw() );
		float dist = Intersection2D_F32.intersection( paraLine, target );
		assertEquals( 1, dist, JgrlConstants.FLOAT_TEST_TOL );

		// should hit dead center, but negative
		paraLine.setSlope( 0, -1 );
		paraLine.setAngle( paraLine.getAngle() + tran.getYaw() );
		dist = Intersection2D_F32.intersection( paraLine, target );
		assertEquals( -1, dist, JgrlConstants.FLOAT_TEST_TOL );

		// should miss it to the left
		paraLine.setSlope( -1.1f, 1 );
		paraLine.setAngle( paraLine.getAngle() + tran.getYaw() );
		dist = Intersection2D_F32.intersection( paraLine, target );
		assertTrue( Float.isNaN( dist ) );

		// should miss it to the right
		paraLine.setSlope( 1.1f, 1 );
		paraLine.setAngle( paraLine.getAngle() + tran.getYaw() );
		dist = Intersection2D_F32.intersection( paraLine, target );
		assertTrue( Float.isNaN( dist ) );
	}
}