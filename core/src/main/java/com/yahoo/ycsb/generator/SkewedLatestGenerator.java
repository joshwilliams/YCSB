/**                                                                                                                                                                                
 * Copyright (c) 2010 Yahoo! Inc. All rights reserved.                                                                                                                             
 *                                                                                                                                                                                 
 * Licensed under the Apache License, Version 2.0 (the "License"); you                                                                                                             
 * may not use this file except in compliance with the License. You                                                                                                                
 * may obtain a copy of the License at                                                                                                                                             
 *                                                                                                                                                                                 
 * http://www.apache.org/licenses/LICENSE-2.0                                                                                                                                      
 *                                                                                                                                                                                 
 * Unless required by applicable law or agreed to in writing, software                                                                                                             
 * distributed under the License is distributed on an "AS IS" BASIS,                                                                                                               
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or                                                                                                                 
 * implied. See the License for the specific language governing                                                                                                                    
 * permissions and limitations under the License. See accompanying                                                                                                                 
 * LICENSE file.                                                                                                                                                                   
 */

package com.yahoo.ycsb.generator;

/**
 * Generate a popularity distribution of items, skewed to favor recent items significantly more than older items.
 */
public class SkewedLatestGenerator extends IntegerGenerator
{
    KeynumGenerator _basis;
    ZipfianGenerator _zipfian;
    private final int _min;

	public SkewedLatestGenerator(KeynumGenerator basis)
	{
		_basis=basis;
        _min = basis.getKeynumForRead();
		_zipfian = new ZipfianGenerator(_min, _min);
		nextInt();
	}

	/**
	 * Generate the next item in the distribution, favoring the items most recently returned by the basis generator.
	 */
	public int nextInt()
	{
        int max = _basis.getKeynumForRead();
        // build a new zipfian generator if we've inserted enough items to make it worth our while.  this is
        // expensive, so we define "worth it" as "item count has doubled."
        if (max - _min > 2 * (_zipfian.base - _min))
            _zipfian = new ZipfianGenerator(_min, max);

        int nextint;
        nextint = max - _zipfian.nextInt();

        assert nextint > _min;
		setLastInt(nextint);
		return nextint;
	}

	public static void main(String[] args)
	{
		SkewedLatestGenerator gen = new SkewedLatestGenerator(new KeynumGenerator(1000));
		for (int i=0; i<Integer.parseInt(args[0]); i++)
		{
			System.out.println(gen.nextString());
		}
	}

	@Override
	public double mean() {
		throw new UnsupportedOperationException("Can't compute mean of non-stationary distribution!");
	}

}
