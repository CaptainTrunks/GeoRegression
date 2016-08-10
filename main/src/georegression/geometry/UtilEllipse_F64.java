/*
 * Copyright (C) 2011-2016, Peter Abeles. All Rights Reserved.
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

import georegression.misc.GrlConstants;
import georegression.struct.line.LineGeneral2D_F64;
import georegression.struct.point.Point2D_F64;
import georegression.struct.point.Vector2D_F64;
import georegression.struct.shapes.EllipseQuadratic_F64;
import georegression.struct.shapes.EllipseRotated_F64;

/**
 * Functions for extracting information from ellipses and converting between different ellipse formats.
 *
 * @author Peter Abeles
 */
public class UtilEllipse_F64 {

	/**
	 * <p>
	 * Convert from quadratic to rotated formats.  Equations taken from [1].
	 * </p>
	 *
	 * <p>
	 * [1] David Eberly "Information About Ellipses", Geometric Tools, LLC 2011
	 * </p>
	 *
	 * @param input Input in quadratic format.
	 * @param output (Optional) Storage for converted format.  Can be null.
	 * @return Ellipse in rotated format.
	 */
	public static EllipseRotated_F64 convert( EllipseQuadratic_F64 input , EllipseRotated_F64 output ) {
		if( output == null )
			output = new EllipseRotated_F64();

		double a11 = input.a;
		double a12 = input.b;
		double a22 = input.c;
		double b1  = 2*input.d;
		double b2  = 2*input.e;
		double c = input.f;

		output.center.x = (a22*b1-a12*b2)/(2*(a12*a12 - a11*a22));
		output.center.y = (a11*b2-a12*b1)/(2*(a12*a12 - a11*a22));

		double k1 = output.center.x;
		double k2 = output.center.y;

		double mu = 1.0/(a11*k1*k1 + 2*a12*k1*k2 + a22*k2*k2 - c);
		double m11 = mu*a11;
		double m12 = mu*a12;
		double m22 = mu*a22;

		double inner = Math.sqrt((m11-m22)*(m11-m22) + 4*m12*m12);
		double l1 = ((m11+m22) + inner)/2.0;
		double l2 = ((m11+m22) - inner)/2.0;

		output.b = 1/(double)Math.sqrt(l1);
		output.a = 1/(double)Math.sqrt(l2);

		// direction of minor axis
		double dx,dy;
		if( m11 >= m22 ) {
			dx = l1-m22;
			dy = m12;
		} else {
			dx = m12;
			dy = l1-m11;
		}

		// direction of major axis
		output.phi = Math.atan2(-dx,dy);
		if( output.phi < -GrlConstants.PId2 ) {
			output.phi += (double)Math.PI;
		} else if( output.phi > GrlConstants.PId2 ) {
			output.phi -= (double)Math.PI;
		}

		return output;
	}

	/**
	 * Convert from rotated to quadratic.
	 *
	 * @param input Input rotated format.
	 * @param output (Optional) Storage for quadratic format.  Can be null.
	 * @return Ellipse in quadratic format.
	 */
	public static EllipseQuadratic_F64 convert( EllipseRotated_F64 input , EllipseQuadratic_F64 output ) {
		if( output == null )
			output = new EllipseQuadratic_F64();

		double x0 = input.center.x;
		double y0 = input.center.y;
		double a = input.a;
		double b = input.b;
		double phi = input.phi;

		double cphi = Math.cos(phi);
		double sphi = Math.sin(phi);
		double cphi2 = cphi*cphi;
		double sphi2 = sphi*sphi;

		double a2 = a*a;
		double b2 = b*b;
		double x02 = x0*x0;
		double y02 = y0*y0;

		// TODO simplfy using more trig identities
		output.a = cphi2/a2 + sphi2/b2;
		output.b = sphi*cphi/a2 - sphi*cphi/b2;
		output.c = sphi2/a2 + cphi2/b2;
		output.d = -x0*cphi2/a2 - y0*sphi*cphi/a2 - x0*sphi2/b2 + y0*sphi*cphi/b2;
		output.e = -x0*sphi*cphi/a2 - y0*sphi2/a2 + x0*sphi*cphi/b2 - y0*cphi2/b2;
		output.f = x02*cphi2/a2 + 2*x0*y0*sphi*cphi/a2 + y02*sphi2/a2 +
				x02*sphi2/b2 - 2*x0*y0*sphi*cphi/b2 + y02*cphi2/b2 - 1;

		return output;
	}

	/**
	 * Computes the value of the quadratic ellipse function at point (x,y). Should equal 0 if the point
	 * is along the ellipse.
	 *
	 * @param x x-coordinate
	 * @param y y-coordinate
	 * @param ellipse Ellipse equation being evaluated.
	 * @return value of ellipse equation at point (x,y)
	 */
	public static double evaluate( double x , double y , EllipseQuadratic_F64 ellipse ) {
		return ellipse.a*x*x + 2*ellipse.b*x*y + ellipse.c*y*y + 2*ellipse.d*x + 2*ellipse.e*y + ellipse.f;
	}

	/**
	 * Computes the value of the quadratic ellipse function at point (x,y).  Should equal 1 if the point is on the
	 * ellipse.
	 * @param x x-coordinate
	 * @param y y-coordinate
	 * @param ellipse Ellipse equation being evaluated.
	 * @return value of ellipse equation at point (x,y)
	 */
	public static double evaluate( double x , double y , EllipseRotated_F64 ellipse ) {

		double cphi = Math.cos(ellipse.phi);
		double sphi = Math.sin(ellipse.phi);

		x -= ellipse.center.x;
		y -= ellipse.center.y;

		double left = (x*cphi + y*sphi);
		double right = (-x*sphi + y*cphi);

		double ll = left/ellipse.a;
		double rr = right/ellipse.b;

		return ll*ll + rr*rr;
	}

	/**
	 * Computes the point on the ellipse at location 't', where t is an angle in radians
	 *
	 * @param t An angle in radians from 0 to 2*PI
	 * @param ellipse Ellipse
	 * @param output (Optional) point on the ellipse .  Can be null.
	 * @return Point on the ellipse
	 */
	public static Point2D_F64 computePoint( double t , EllipseRotated_F64 ellipse , Point2D_F64 output ) {
		if( output == null )
			output = new Point2D_F64();

		double ct = Math.cos(t);
		double st = Math.sin(t);
		double cphi = Math.cos(ellipse.phi);
		double sphi = Math.sin(ellipse.phi);

		// coordinate in ellipse frame
		double x = ellipse.a*ct;
		double y = ellipse.b*st;

		// put into global frame
		output.x = ellipse.center.x + x*cphi - y*sphi;
		output.y = ellipse.center.y + x*sphi + y*cphi;

		return output;
	}

	/**
	 * Computes the value of 't' used to specify a point's location
	 *
	 * @param p Point on the ellipse
	 * @param ellipse Ellipse
	 * @return Angle from -pi to pi
	 */
	public static double computeAngle( Point2D_F64 p , EllipseRotated_F64 ellipse ) {
		// put point into ellipse's reference frame
		double ce = Math.cos(ellipse.phi);
		double se = Math.sin(ellipse.phi);

		// world into ellipse frame
		double xc = p.x - ellipse.center.x;
		double yc = p.y - ellipse.center.y;

		double x =  ce*xc + se*yc;
		double y = -se*xc + ce*yc;

		return Math.atan2( y/ellipse.b , x/ellipse.a );
	}

	/**
	 * Computes the tangent to the ellipse at the specified location
	 *
	 * @param t Location on the ellipse.  Radians
	 * @param ellipse Ellipse equation
	 * @param output Optional storage for tangent
	 * @return The tangent
	 */
	public static Vector2D_F64 computeTangent( double t ,
											   EllipseRotated_F64 ellipse ,
											   Vector2D_F64 output  ) {
		if( output == null )
			output = new Vector2D_F64();

		double ct = Math.cos(t);
		double st = Math.sin(t);
		double cphi = Math.cos(ellipse.phi);
		double sphi = Math.sin(ellipse.phi);

		// point in ellipse frame multiplied by b^2 and a^2
		double x = ellipse.a*ct*ellipse.b*ellipse.b;
		double y = ellipse.b*st*ellipse.a*ellipse.a;

		// rotate vector normal into world frame
		double rx = x*cphi - y*sphi;
		double ry = x*sphi + y*cphi;

		// normalize and change into tangent
		double r = Math.sqrt(rx*rx + ry*ry);

		output.x = -ry/r;
		output.y = rx/r;

		return output;
	}

	/**
	 * Finds the two liens which are tangent to the ellipse and pass through the point.  The point is assumed to be
	 * outside of the ellipse.
	 *
	 * @param pt Point which the liens will pass though
	 * @param ellipse The ellipse which the lines will be tangent to
	 * @param lineA (output) line
	 * @param lineB (output) line
	 */
	public static boolean tangentLines(Point2D_F64 pt , EllipseRotated_F64 ellipse ,
									LineGeneral2D_F64 lineA , LineGeneral2D_F64 lineB )
	{
		// Derivation:
		// Compute the tangent at only point along the ellipse by computing dy/dx
		//    x*b^2/(y*a^2) or - x*b^2/(y*a^2)  are the possible solutions for the tangent
		// The slope of the line and the gradient are the same, so this is true:
		//   y - y'     -x*b^2
		//  -------  =  -------
		//   x - x'      y*a^2
		//
		//  (x,y) is point on ellipse, (x',y') is pt that lines pass through
		//
		//  that becomes
		//  y^2*a^2 + x^2*b^2 = x'*x*b^2 + y'*y*a^2
		//  use the equation for the ellipse (centered and aligned at origin)
		//  a^2*b^2 =  x'*x*b^2 + y'*y*a^2
		//
		// solve for y
		// plug into ellipse equation
		// solve for x, which is a quadratic equation

		// translate and rotate into ellipse reference frame
		double cphi = Math.cos(ellipse.phi);
		double sphi = Math.sin(ellipse.phi);

		double xt = pt.x*cphi - pt.y*sphi - ellipse.center.x;
		double yt = pt.x*sphi + pt.y*cphi - ellipse.center.y;

		// solve
		double a2 = ellipse.a*ellipse.a;
		double b2 = ellipse.b*ellipse.b;

		// quadratic equation for the two variants.
		// solving for x
		double aa0 = yt*yt + xt*xt*b2/a2;
		double bb0 = -2.0*xt*b2;
		double cc0 = a2*(b2-yt*yt);

		double descriminant0 = bb0*bb0 - 4.0*aa0*cc0;

		// solving for y
		double aa1 = xt*xt + yt*yt*a2/b2;
		double bb1 = -2.0*yt*a2;
		double cc1 = b2*(a2-xt*xt);

		double descriminant1 = bb1*bb1 - 4.0*aa1*cc1;

		double x0,y0, x1,y1;
		if( descriminant0 > descriminant1 ) {
			double right = Math.sqrt(descriminant0);

			x0 = (-bb0 + right)/(2.0*aa0);
			x1 = (-bb0 - right)/(2.0*aa0);

			y0 = b2/yt - xt*x0*b2/(yt*a2);
			y1 = b2/yt - xt*x1*b2/(yt*a2);

		} else {
			double right = Math.sqrt(descriminant1);

			y0 = (-bb1 + right)/(2.0*aa1);
			y1 = (-bb1 - right)/(2.0*aa1);

			x0 = a2/xt - yt*y0*a2/(xt*b2);
			x1 = a2/xt - yt*y1*a2/(xt*b2);
		}

		// convert the lines back into world space
		double xx0 = x0*cphi - y0*sphi + ellipse.center.x;
		double yy0 = x0*sphi + y0*cphi + ellipse.center.y;

		double xx1 = x1*cphi - y1*sphi + ellipse.center.x;
		double yy1 = x1*sphi + y1*cphi + ellipse.center.y;

		// convert into a line
		lineA.A = pt.y - yy0;
		lineA.B = xx0 - pt.x;
		lineA.C = -(lineA.A*pt.x + lineA.B*pt.y);
		lineA.normalize();

		lineB.A = pt.y - yy1;
		lineB.B = xx1 - pt.x;
		lineB.C = -(lineB.A*pt.x + lineB.B*pt.y);
		lineB.normalize();

		return true;
	}
}
