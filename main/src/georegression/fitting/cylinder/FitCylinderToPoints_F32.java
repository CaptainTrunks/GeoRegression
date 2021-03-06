/*
 * Copyright (C) 2011-2015, Peter Abeles. All Rights Reserved.
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

package georegression.fitting.cylinder;

import georegression.struct.point.Point3D_F32;
import georegression.struct.shapes.Cylinder3D_F32;
import org.ddogleg.fitting.modelset.ModelFitter;
import org.ddogleg.optimization.FactoryOptimization;
import org.ddogleg.optimization.UnconstrainedLeastSquares;

import java.util.List;

/**
 * {@link UnconstrainedLeastSquares} fitting of 3D points to a {@link Cylinder3D_F32 cylinder}.
 *
 * @author Peter Abeles
 */
public class FitCylinderToPoints_F32 implements ModelFitter<Cylinder3D_F32,Point3D_F32> {

	// functions used by non-linear least squares solver
	private CylinderToPointSignedDistance_F32 function = new CylinderToPointSignedDistance_F32();
	private CylinderToPointSignedDistanceJacobian_F32 jacobian = new CylinderToPointSignedDistanceJacobian_F32();

	// The solver
	private UnconstrainedLeastSquares optimizer;

	// need to convert sphere to float[]
	private /**/double[] param = new /**/double[7];

	// maximum number of iterations
	private int maxIterations;

	// tolerances for optimization
	private /**/double ftol;
	private /**/double gtol;

	// used to convert float[] into shape parameters
	private CodecCylinder3D_F32 codec = new CodecCylinder3D_F32();

	/**
	 * Constructor which provides access to all tuning parameters
	 *
	 * @param optimizer Optimization algorithm
	 * @param maxIterations Maximum number of iterations that the optimizer can perform. Try 100
	 * @param ftol Convergence tolerance. See {@link UnconstrainedLeastSquares}.
	 * @param gtol Convergence tolerance. See {@link UnconstrainedLeastSquares}.
	 */
	public FitCylinderToPoints_F32(UnconstrainedLeastSquares optimizer,
								   int maxIterations, /**/double ftol, /**/double gtol) {
		this.optimizer = optimizer;
		this.maxIterations = maxIterations;
		this.ftol = ftol;
		this.gtol = gtol;
	}

	/**
	 * Simplified constructor.  Only process access to the maximum number of iterations.
	 * @param maxIterations Maximum number of iterations.  Try 100
	 */
	public FitCylinderToPoints_F32( int maxIterations ) {
		this(FactoryOptimization.leastSquaresLM(1e-3, false),maxIterations,1e-12,0);
	}

	@Override
	public boolean fitModel(List<Point3D_F32> dataSet, Cylinder3D_F32 initial, Cylinder3D_F32 found) {

		codec.encode(initial,param);

		function.setPoints(dataSet);
		jacobian.setPoints(dataSet);

		optimizer.setFunction(function,jacobian);
		optimizer.initialize(param,ftol,gtol);

		for( int i = 0; i < maxIterations; i++ ) {
			if( optimizer.iterate() )
				break;
		}

		codec.decode(optimizer.getParameters(), found);

		return true;
	}
}
