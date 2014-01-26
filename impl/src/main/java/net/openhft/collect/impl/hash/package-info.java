/*
 * Copyright 2014 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

/**
 * SO class name suffix stands for "Specific Operations", GO - "Generic Operations".
 * Classes which differ only in suffix represent the same abstraction. Usually,
 * there are different "specific operations" classes for primitive and object params, and common
 * "generic operations". Thus this segregation is for reducing repetitive _template_ code only.
 * (DRY!!)
 *
 * Some "Generic Operations" are so generic that could be used to template tree maps/sets in future.
 */
package net.openhft.collect.impl.hash;