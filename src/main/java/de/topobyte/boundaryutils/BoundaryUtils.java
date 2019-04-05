// Copyright 2019 Sebastian Kuerten
//
// This file is part of boundary-utils.
//
// boundary-utils is free software: you can redistribute it and/or modify
// it under the terms of the GNU Lesser General Public License as published by
// the Free Software Foundation, either version 3 of the License, or
// (at your option) any later version.
//
// boundary-utils is distributed in the hope that it will be useful,
// but WITHOUT ANY WARRANTY; without even the implied warranty of
// MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
// GNU Lesser General Public License for more details.
//
// You should have received a copy of the GNU Lesser General Public License
// along with boundary-utils. If not, see <http://www.gnu.org/licenses/>.

package de.topobyte.boundaryutils;

import java.util.Collection;
import java.util.List;

import com.vividsolutions.jts.geom.Geometry;

import de.topobyte.adt.graph.Graph;
import de.topobyte.jsi.GenericRTree;
import de.topobyte.jsijts.JsiAndJts;

public class BoundaryUtils
{

	public static <T> Graph<T> createNeighborGraph(Collection<T> objects,
			GeometrySource<T> geometrySource)
	{
		// Insert all object into an RTree and as nodes into the graph
		GenericRTree<T> tree = new GenericRTree<>();
		Graph<T> graph = new Graph<>();
		for (T object : objects) {
			Geometry boundary = geometrySource.getGeometry(object);
			tree.add(JsiAndJts.toRectangle(boundary), object);
			graph.addNode(object);
		}
		// Find neighbors
		for (T object : objects) {
			Geometry boundary = geometrySource.getGeometry(object);
			List<T> candidates = tree
					.intersectionsAsList(JsiAndJts.toRectangle(boundary));
			for (T candidate : candidates) {
				if (candidate == object) {
					continue;
				}
				Geometry otherBoundary = geometrySource.getGeometry(candidate);
				if (boundary.intersects(otherBoundary)) {
					graph.addEdge(object, candidate);
					graph.addEdge(candidate, object);
				}
			}
		}
		return graph;
	}

}
