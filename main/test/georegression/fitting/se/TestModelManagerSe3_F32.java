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

package georegression.fitting.se;

import georegression.misc.GrlConstants;
import georegression.struct.se.Se3_F32;
import org.ejml.ops.MatrixFeatures;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * @author Peter Abeles
 */
public class TestModelManagerSe3_F32 {

	@Test
	public void createModelInstance() {
		ModelManagerSe3_F32 alg = new ModelManagerSe3_F32();

		assertTrue(alg.createModelInstance() != null);
	}

	@Test
	public void copyModel() {
		ModelManagerSe3_F32 alg = new ModelManagerSe3_F32();

		Se3_F32 model = new Se3_F32();
		Se3_F32 found = new Se3_F32();

		model.T.set(1,2,3);
		model.getR().set(3,3,true,1,2,3,4,5,6,7,8,9);

		alg.copyModel(model,found);

		assertTrue(MatrixFeatures.isEquals(model.getR(),found.getR()));
		assertEquals(model.T.x,found.T.x,GrlConstants.FLOAT_TEST_TOL);
		assertEquals(model.T.y,found.T.y,GrlConstants.FLOAT_TEST_TOL);
		assertEquals(model.T.z,found.T.z,GrlConstants.FLOAT_TEST_TOL);
	}

}
