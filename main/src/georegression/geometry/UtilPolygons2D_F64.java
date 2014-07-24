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

package georegression.geometry;

import georegression.metric.UtilAngle;
import georegression.struct.point.Point2D_F64;
import georegression.struct.shapes.Quadrilateral_F64;
import georegression.struct.shapes.Rectangle2D_F64;
import georegression.struct.shapes.RectangleLength2D_I32;

import java.util.List;

/**
 * Various functions related to polygons.
 *
 * @author Peter Abeles
 */
public class UtilPolygons2D_F64 {

	/**
	 * Converts a rectangle into a quadrilateral
	 *
	 * @param input Rectangle.
	 * @param output Quadrilateral.  Modified.
	 */
	public static void convert( Rectangle2D_F64 input , Quadrilateral_F64 output ) {
		output.a.x = input.p0.x;
		output.a.y = input.p0.y;

		output.b.x = input.p1.x;
		output.b.y = input.p0.y;

		output.c.x = input.p1.x;
		output.c.y = input.p1.y;

		output.d.x = input.p0.x;
		output.d.y = input.p1.y;
	}

	/**
	 * Converts a rectangle into a quadrilateral
	 *
	 * @param input Rectangle.
	 * @param output Quadrilateral.  Modified.
	 */
	public static void convert( RectangleLength2D_I32 input , Quadrilateral_F64 output ) {
		output.a.x = input.x0;
		output.a.y = input.y0;

		output.b.x = input.x0+input.width-1;
		output.b.y = input.y0;

		output.c.x = input.x0+input.width-1;
		output.c.y = input.y0+input.height-1;

		output.d.x = input.x0;
		output.d.y = input.y0+input.height-1;
	}

	/**
	 * Finds the minimum area bounding rectangle around the quadrilateral.
	 *
	 * @param quad (Input) Quadrilateral
	 * @param rectangle (Output) Minimum area rectangle
	 */
	public static void bounding( Quadrilateral_F64 quad , Rectangle2D_F64 rectangle ) {

		rectangle.p0.x = Math.min(quad.a.x,quad.b.x);
		rectangle.p0.x = Math.min(rectangle.p0.x,quad.c.x);
		rectangle.p0.x = Math.min(rectangle.p0.x,quad.d.x);

		rectangle.p0.y = Math.min(quad.a.y,quad.b.y);
		rectangle.p0.y = Math.min(rectangle.p0.y,quad.c.y);
		rectangle.p0.y = Math.min(rectangle.p0.y,quad.d.y);

		rectangle.p1.x = Math.max(quad.a.x,quad.b.x);
		rectangle.p1.x = Math.max(rectangle.p1.x,quad.c.x);
		rectangle.p1.x = Math.max(rectangle.p1.x,quad.d.x);

		rectangle.p1.y = Math.max(quad.a.y,quad.b.y);
		rectangle.p1.y = Math.max(rectangle.p1.y,quad.c.y);
		rectangle.p1.y = Math.max(rectangle.p1.y,quad.d.y);
	}

	/**
	 * Returns true if the polygon is ordered in a counter-clockwise order.  This is done by summing up the interior
	 * angles.
	 * 
	 * @param polygon List of ordered points which define a polygon
	 * @return true if CCW and false if CW
	 */
	public static boolean isCCW( List<Point2D_F64> polygon ) {
		double total = 0;

		final int N = polygon.size();
		for (int i = 0; i <= N; i++) {
			Point2D_F64 a = polygon.get(i%N);
			Point2D_F64 b = polygon.get((i+1)%N);
			Point2D_F64 c = polygon.get((i+2)%N);

			double angleBA = Math.atan2(a.y-b.y,a.x-b.x);
			double angleBC = Math.atan2(c.y-b.y,c.x-b.x);

			total += UtilAngle.minus(angleBA, angleBC);
		}


		return total > 0;
	}

//	public static boolean isCCW( Point2D_F64 a, Point2D_F64 b , Point2D_F64 c ) {
//		double angleAB = Math.atan2(b.y-a.y,b.x-a.x);
//		double angleAC = Math.atan2(c.y-a.y,c.x-a.x);
//
//		return UtilAngle.distanceCCW(angleAB,angleAC) < Math.PI;
//	}
}